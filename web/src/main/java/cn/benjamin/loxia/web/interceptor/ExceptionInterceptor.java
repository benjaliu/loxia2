package cn.benjamin.loxia.web.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.struts2.interceptor.TokenInterceptor;
import org.apache.struts2.util.TokenHelper;

import cn.benjamin.loxia.exception.BusinessException;
import cn.benjamin.loxia.web.annotation.DataResponse;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ExceptionHolder;
import com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

public class ExceptionInterceptor extends ExceptionMappingInterceptor {

	/**
	 *
	 */
	private static final long serialVersionUID = -108722874114862093L;
	private boolean debug = false;
	
	@SuppressWarnings("unchecked")
	protected Method getActionMethod(Class actionClass, String methodName) throws NoSuchMethodException {
        Method method;
        try {
            method = actionClass.getMethod(methodName, new Class[0]);
        } catch (NoSuchMethodException e) {
            // hmm -- OK, try doXxx instead
            try {
                String altMethodName = "do" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                method = actionClass.getMethod(altMethodName, new Class[0]);
            } catch (NoSuchMethodException e1) {
                // throw the original one
                throw e;
            }
        }
        return method;
    }

	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String result = super.intercept(invocation);
		Object action = invocation.getAction();
		DataResponse dr = action.getClass().getAnnotation(DataResponse.class);
		if(dr != null){
			if(TokenInterceptor.INVALID_TOKEN_CODE.equals(result)){
				//duplicate submit error here
				//construct one exception for jsonresult
				String errorMessage = LocalizedTextUtil.findText(this.getClass(), "struts.messages.invalid.token",
		                invocation.getInvocationContext().getLocale(),
		                "The form has already been processed or no token was supplied, please try again.", new Object[0]);
				
				Map request  = (Map)invocation.getInvocationContext().get("request");
				Map session = invocation.getInvocationContext().getSession();
				Map<String,Object> exceptionMap = new HashMap<String, Object>();
				exceptionMap.put("invalidToken", true);
				exceptionMap.put("errorMessages", Arrays.asList(errorMessage));
				synchronized (session) {
					exceptionMap.put("token", TokenHelper.setToken());
				}
				request.put("exception", exceptionMap);
			}
			return dr.value();
		}else
			return result;		
	}	
	
	private List<String> getErrMessage(ActionInvocation invocation, ExceptionHolder exceptionHolder){
		List<String> errMessage = new ArrayList<String>();		
		if(exceptionHolder.getException() instanceof BusinessException){
			errMessage = getBusiExceptionMessage(invocation,(BusinessException)exceptionHolder.getException());
		}else{
			errMessage.add(getMessage(invocation,"system_error", null));
		}
		return errMessage;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void publishException(ActionInvocation invocation, ExceptionHolder exceptionHolder) {		
		Map request  = (Map)invocation.getInvocationContext().get("request");
		Map<String,Object> exceptionMap = new HashMap<String, Object>();
		exceptionMap.put("exception", exceptionHolder.getException());
		exceptionMap.put("errorMessages", getErrMessage(invocation, exceptionHolder));
		exceptionMap.put("stackTrace", exceptionHolder.getExceptionStack());
		request.put("exception", exceptionMap);
		if(! (exceptionHolder.getException() instanceof BusinessException)){
			if(debug){
				exceptionHolder.getException().printStackTrace();
			}
		}
    }

	private List<String> getBusiExceptionMessage(ActionInvocation invocation, BusinessException e){		
		List<String> errors = new ArrayList<String>();
		BusinessException be = e;
		while(be != null){
			String msgKey = "business_exception_" + be.getErrorCode();
			errors.add(getMessage(invocation, msgKey, be.getArgs()));
			be = be.getLinkedException();
		}

		return errors;
	}

	private String getMessage(ActionInvocation invocation, String msgKey, Object[] args){
		Locale locale = invocation.getInvocationContext().getLocale();
		return LocalizedTextUtil.findText(invocation.getAction().getClass(),
				msgKey, locale, msgKey, args);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
