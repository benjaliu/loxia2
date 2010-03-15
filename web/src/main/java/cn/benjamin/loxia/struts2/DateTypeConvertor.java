package cn.benjamin.loxia.struts2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public class DateTypeConvertor extends StrutsTypeConverter {

	@SuppressWarnings("unchecked")
	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if(values == null || values.length == 0) return null;
		String dateFormat = LoxiaWebSettings.getInstance().get(LoxiaWebConstants.DATE_PATTERN);
		DateFormat df = new SimpleDateFormat(dateFormat);
		try {
			return df.parse(values[0]);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new TypeConversionException(values[0] + " is not one valid date.");
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public String convertToString(Map context, Object o) {
		if(!(o instanceof Date)) throw new TypeConversionException();
		String dateFormat = LoxiaWebSettings.getInstance().get(LoxiaWebConstants.DATE_PATTERN);
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format((Date)o);
	}

}
