package cn.benjamin.loxia.struts2.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.benjamin.loxia.dao.OperatingUnitDao;
import cn.benjamin.loxia.exception.BusinessException;
import cn.benjamin.loxia.exception.PreserveErrorCode;
import cn.benjamin.loxia.security.LoxiaGrantedAuthority;
import cn.benjamin.loxia.security.LoxiaUserDetails;
import cn.benjamin.loxia.struts2.BaseAction;
import cn.benjamin.loxia.struts2.BaseProfileAction;
import cn.benjamin.loxia.struts2.LoxiaUserDetailsAware;
import cn.benjamin.loxia.web.annotation.Acl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

public class AuthorizationInterceptor extends AbstractInterceptor implements StrutsStatics {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6983132502906240133L;
	
	static final Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);

	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		logger.debug("Start AuthenticationInterceptor");
		final Object action = invocation.getAction();
		final ActionContext context = invocation.getInvocationContext();
		WebApplicationContext ctx = WebApplicationContextUtils.
			getWebApplicationContext((ServletContext) context.get(SERVLET_CONTEXT));
		HttpServletRequest request = (HttpServletRequest)context.get(HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse)context.get(HTTP_RESPONSE);
		
		OperatingUnitDao operatingUnitDao = (OperatingUnitDao)ctx.getBean("loxiaOperatingUnitDao");
		
		String strMethod = invocation.getProxy().getMethod();
		Method m = getActionMethod(action.getClass(), strMethod);
		Acl acl = m.getAnnotation(Acl.class);
		if(acl == null)
			acl = action.getClass().getAnnotation(Acl.class);
				
		boolean needCheck = true;
		boolean needCredential = true;
		if(acl == null){
			needCheck = false;
			needCredential = false;
		}else if(acl.value().length == 0
				|| Arrays.asList(acl.value()).contains(""))
			needCheck = false;
		if(logger.isDebugEnabled()){
			if(needCredential)
				logger.debug("Credential is required.");
			logger.debug("Current ACL:{}", needCheck ? acl : "");
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(needCredential && authentication == null){
			logger.error("Session timeout.");
			throw new BusinessException(PreserveErrorCode.SESSION_TIMEOUT);
		}
		
		if(needCheck){
			BaseProfileAction act = (BaseProfileAction)action;
									
			LoxiaUserDetails userDetails = (LoxiaUserDetails)authentication.getPrincipal();
			logger.debug("Current Principal:" + userDetails);
			String entryAcl = act.getAcl();
			if(entryAcl != null){
				userDetails.setCurrentOu(null);
				logger.debug("Function Entrance... Organization need to repick");
				
				for(GrantedAuthority auth: userDetails.getAuthorities()){
					LoxiaGrantedAuthority lauth = (LoxiaGrantedAuthority)auth;
					if(lauth.getAuthority().equals(entryAcl)){
						userDetails.setCurrentAuthority(lauth);
						break;
					}
				}
				if(userDetails.getCurrentAuthority() == null || 
						userDetails.getCurrentAuthority().getOuIds().size() == 0){
					logger.error("No sufficicent privilege.");
					throw new BusinessException(PreserveErrorCode.NO_SUFFICICENT_PRIVILEGE);
				}else{
					if(userDetails.getCurrentAuthority().
							getOuIds().size() == 1){
						userDetails.setCurrentOu(operatingUnitDao.getByPrimaryKey(
								userDetails.getCurrentAuthority().getOuIds().iterator().next()));
					}else{
						logger.debug("Redirect Invocation");
						
						String url = request.getRequestURI();
						Enumeration<String> paramNames = request.getParameterNames();
						StringBuffer paramsSb = new StringBuffer();
						while (paramNames.hasMoreElements()) {
							String name = (String) paramNames.nextElement();
							if (!"acl".equalsIgnoreCase(name)){
								paramsSb.append(name + "=" + request.getParameter(name) + "&");
							}
						}
						if (paramsSb.length() > 0){
							paramsSb.deleteCharAt(paramsSb.length()-1);
							url = url + "?" + paramsSb.toString();
						}
						request.getSession().setAttribute(BaseAction.FOLLOWING_URL_AFTER_OPERATING_UNIT_PICKUP, url);
						response.sendRedirect(request.getContextPath() + "/operatingunitpickup.do");
						return null;
					}
				}
			}else{
				if(act.getSelectedOuId() != null){
					//set Current OperatingUint in up
					userDetails.setCurrentOu(operatingUnitDao.getByPrimaryKey(act.getSelectedOuId()));
				}else{
					if(!userDetails.checkAuthority(acl.value())){
						logger.error("No sufficicent privilege.");
						throw new BusinessException(PreserveErrorCode.NO_SUFFICICENT_PRIVILEGE);
					}
				}					
			}
						
		}
		
		if(authentication != null && authentication.getPrincipal()instanceof LoxiaUserDetails){		
			LoxiaUserDetails userDetails = (LoxiaUserDetails)authentication.getPrincipal();
			if(action instanceof LoxiaUserDetailsAware){
				LoxiaUserDetailsAware aware = (LoxiaUserDetailsAware)action;
				aware.setLoxiaUserDetails(userDetails);
			}
			if(userDetails.getCurrentOu() == null){
				logger.debug("Set CurrentOu for develope purpose. Current Ou:{}",userDetails.getUser().getOu().getName());
				userDetails.setCurrentOu(userDetails.getUser().getOu());
			}
		}
				
		return invocation.invoke();
		
	}
	
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
	
	protected String getMessage(Locale locale, String key, Object[] args){
		return LocalizedTextUtil.findText(this.getClass(), key, locale, key, args);
	}
}
