package loxia.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilterEntryPoint;

public class LoxiaAjaxAwareFormAuthenticationEntryPoint extends
		AuthenticationProcessingFilterEntryPoint {
		
	@Override
	public void commence(ServletRequest request, ServletResponse response,
			AuthenticationException authException)
			throws IOException, ServletException {
		if(((HttpServletRequest)request).getHeader("X-Requested-With") != null){
			((HttpServletResponse)response).sendError(601, "");
		}else
			super.commence(request, response, authException);
	}
}
