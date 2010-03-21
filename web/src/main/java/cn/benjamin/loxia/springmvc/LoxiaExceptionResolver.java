package cn.benjamin.loxia.springmvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import cn.benjamin.loxia.exception.BusinessException;

public class LoxiaExceptionResolver extends SimpleMappingExceptionResolver {

	private static final String BUSINESS_EXCEPTION_PREFIX = "business_exception"; 
	
	@Resource
	private ApplicationContext context;
	
	@Override
	protected ModelAndView getModelAndView(String viewName, Exception ex,
			HttpServletRequest request) {
		if(ex instanceof BusinessException){
			BusinessException bex = (BusinessException)ex;
			ex = encode(bex);
		}
		if(((HttpServletRequest)request).getHeader("X-Requested-With") != null){
			return getModelAndView("json", ex);
		}else		
			return super.getModelAndView(viewName, ex, request);
	}

	private BusinessException encode(BusinessException ex){
		String key = BUSINESS_EXCEPTION_PREFIX + ex.getErrorCode();
		BusinessException result = new BusinessException(ex.getErrorCode(), 
				context.getMessage(key, ex.getArgs(),key, LocaleContextHolder.getLocale()));
		result.setArgs(ex.getArgs());
		if(ex.getLinkedException() != null){
			result.setLinkedException(encode(ex.getLinkedException()));
		}
		return result;
	}
}
