package cn.benjamin.loxia.struts2.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import cn.benjamin.loxia.struts2.taglib.model.DateField;

import com.opensymphony.xwork2.util.ValueStack;

public class DateFieldTag extends TextFieldTag {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -1032616530307436163L;

    protected String min;
    protected String max;
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new DateField(stack,req,res);
	}
	
	protected void populateParams() {
        super.populateParams();
        DateField dateField = ((DateField) component);
        dateField.setMin(min);
        dateField.setMax(max);
	}

	public void setMin(String min) {
		this.min = min;
	}

	public void setMax(String max) {
		this.max = max;
	}
	
}
