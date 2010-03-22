package cn.benjamin.loxia.struts2.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.interceptor.TokenInterceptor;
import org.apache.struts2.util.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.benjamin.loxia.exception.BusinessException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ExceptionHolder;
import com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

public class ExceptionInterceptor extends ExceptionMappingInterceptor {

	/**
	 *
	 */
	private static final long serialVersionUID = -108722874114862093L;
	private static final Logger logger = LoggerFactory.getLogger(ExceptionInterceptor.class);
	
	private boolean debug = false;

	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		final ActionContext context = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest)context.get(StrutsStatics.HTTP_REQUEST);
		boolean isXhr = (request.getHeader("X-Requested-With") != null);
		String result = super.intercept(invocation);
		if(isXhr){
			logger.debug("This is one xhrequest.");
			if(TokenInterceptor.INVALID_TOKEN_CODE.equals(result)){
				//duplicate submit error here
				//construct one exception for jsonresult
				String errorMessage = LocalizedTextUtil.findText(this.getClass(), "struts.messages.invalid.token",
		                invocation.getInvocationContext().getLocale(),
		                "The form has already been processed or no token was supplied, please try again.", new Object[0]);
				
				Map req  = (Map)context.get("request");
				Map session = context.getSession();
				Map<String,Object> exceptionMap = new HashMap<String, Object>();
				exceptionMap.put("invalidToken", true);
				exceptionMap.put("errorMessages", Arrays.asList(errorMessage));
				synchronized (session) {
					exceptionMap.put("token", TokenHelper.setToken());
				}
				req.put("exception", exceptionMap);
			}
			return "json";
		}else{
			return result;
		}		
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
