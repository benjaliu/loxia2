package cn.benjamin.loxia.struts2.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.benjamin.loxia.struts2.taglib.model.TextField;

import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

public class TextFieldTag extends LoxiaAbstractUITag {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6874457629561510222L;
	
    protected String readonly;
    protected String checkmaster;
    protected String maxlength;
    protected String size;
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new TextField(stack,req,res);
	}
	
	protected void populateParams() {
        super.populateParams();
        TextField textField = ((TextField) component);
        textField.setCheckmaster(checkmaster);
        textField.setReadonly(readonly);
        textField.setMaxlength(maxlength);
        textField.setSize(size);
	}

	public void setCheckmaster(String checkmaster) {
		this.checkmaster = checkmaster;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
}
