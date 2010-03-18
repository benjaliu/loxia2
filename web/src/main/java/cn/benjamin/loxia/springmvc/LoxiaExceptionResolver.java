package cn.benjamin.loxia.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class LoxiaExceptionResolver extends SimpleMappingExceptionResolver {

	@Override
	protected ModelAndView getModelAndView(String viewName, Exception ex,
			HttpServletRequest request) {
		if(((HttpServletRequest)request).getHeader("X-Requested-With") != null){
			return getModelAndView("json", ex);
		}else		
			return super.getModelAndView(viewName, ex, request);
	}

}
