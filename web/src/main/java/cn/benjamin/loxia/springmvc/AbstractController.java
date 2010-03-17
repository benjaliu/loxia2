package cn.benjamin.loxia.springmvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import cn.benjamin.loxia.web.LoxiaWebConstants;
import cn.benjamin.loxia.web.LoxiaWebSettings;

public abstract class AbstractController {
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(LoxiaWebSettings.getInstance().
        		get(LoxiaWebConstants.DATE_PATTERN));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}
