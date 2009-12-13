package cn.benjamin.loxia.web.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.benjamin.loxia.dao.OperatingUnitDao;
import cn.benjamin.loxia.dao.UserDao;
import cn.benjamin.loxia.security.LoxiaGrantedAuthority;
import cn.benjamin.loxia.security.LoxiaUserDetails;
import cn.benjamin.loxia.web.BaseProfileAction;
import cn.benjamin.loxia.web.LoxiaUserDetailsAware;
import cn.benjamin.loxia.web.annotation.Acl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

public class AuthenticationInterceptor extends AbstractInterceptor implements StrutsStatics {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6983132502906240133L;
	
	static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		logger.debug("Start AuthenticationInterceptor");
		final Object action = invocation.getAction();
		final ActionContext context = invocation.getInvocationContext();
		WebApplicationContext ctx = WebApplicationContextUtils.
			getWebApplicationContext((ServletContext) context.get(SERVLET_CONTEXT));
		HttpServletRequest request = (HttpServletRequest)context.get(HTTP_REQUEST);
		
		UserDao userDao = (UserDao)ctx.getBean("userDao");
		OperatingUnitDao operatingUnitDao = (OperatingUnitDao)ctx.getBean("OperatingUnitDao");
		
		String strMethod = invocation.getProxy().getMethod();
		Method m = getActionMethod(action.getClass(), strMethod);
		Acl acl = m.getAnnotation(Acl.class);
		if(acl == null)
			acl = action.getClass().getAnnotation(Acl.class);
				
		boolean needCheck = true;
		if(acl == null || acl.value().length == 0
				|| Arrays.asList(acl.value()).contains(""))
			needCheck = false;
		logger.debug("Current ACL:{}", needCheck ? acl : "");
		
		Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(action instanceof LoxiaUserDetailsAware && object instanceof LoxiaUserDetails){
			LoxiaUserDetailsAware aware = (LoxiaUserDetailsAware)action;
			aware.setLoxiaUserDetails((LoxiaUserDetails)object);
		}
		if(needCheck){
			BaseProfileAction act = (BaseProfileAction)action;
									
			LoxiaUserDetails userDetails = (LoxiaUserDetails)object;
			
			String entryAcl = act.getAcl();
			if(entryAcl != null){
				userDetails.setCurrentOu(null);
				logger.debug("Function Entrance... Organization need to repick");
				
				LoxiaGrantedAuthority lga = null;
				for(GrantedAuthority auth: userDetails.getAuthorities()){
					LoxiaGrantedAuthority lauth = (LoxiaGrantedAuthority)auth;
					if(lauth.getAuthority().equals(entryAcl)){
						lga = lauth;
						break;
					}
				}
				if(lga == null || lga.getOuIds().size() == 0){
					//TODO no privilege
				}else{
					if(lga.getOuIds().size() == 1){
						userDetails.setCurrentOu(operatingUnitDao.getByPrimaryKey(lga.getOuIds().iterator().next()));
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
						
						/*request.setAttribute(BaseAction.ORG_LIST,
								orgList);
						request.setAttribute(BaseAction.FOLLOW_URL,
								url);
						return BaseAction.CHOOSE_ORG;*/
					}
				}
			}else{
				if(act.getSelectedOuId() != null){
					//set Current OperatingUint in up
					userDetails.setCurrentOu(operatingUnitDao.getByPrimaryKey(act.getSelectedOuId()));
				}else{
					if(!userDetails.checkAuthority(acl.value())){
						//TODO no sufficient privilege
					}
				}					
			}
						
		}else{
			
		}
		
		if(object instanceof LoxiaUserDetails){		
			LoxiaUserDetails userDetails = (LoxiaUserDetails)object;
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
