package loxia.springmvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import loxia.support.LoxiaSupportConstants;
import loxia.support.LoxiaSupportSettings;

public abstract class AbstractController {
	
	@Resource
	protected ApplicationContext context;
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(LoxiaSupportSettings.getInstance().
        		get(LoxiaSupportConstants.DATE_PATTERN));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        initBinderInternal(binder);
    }
	
	protected void initBinderInternal(WebDataBinder binder){
		//can be overrided to add new bindings
	}
	
	public String getMessage(String key){
		return getMessage(key,null);
	}
	
	public String getMessage(String key, Object[] args){
		return context.getMessage(key, args, key, LocaleContextHolder.getLocale());
	}
}
