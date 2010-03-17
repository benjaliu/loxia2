package cn.benjamin.loxia.springmvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.benjamin.loxia.security.LoxiaUserDetails;
import cn.benjamin.loxia.struts2.LoxiaUserDetailsAware;

public class LoxiaUserDetailsInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if(handler instanceof LoxiaUserDetailsAware){
			LoxiaUserDetailsAware aware = (LoxiaUserDetailsAware)handler;
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(authentication != null && authentication.getPrincipal()instanceof LoxiaUserDetails){
				LoxiaUserDetails userDetails = (LoxiaUserDetails)authentication.getPrincipal();
				aware.setLoxiaUserDetails(userDetails);
			}			
		}
		return true;
	}

}
