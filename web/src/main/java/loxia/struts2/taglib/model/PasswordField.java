package loxia.struts2.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import loxia.struts2.taglib.annotation.LoxiaTag;

import com.opensymphony.xwork2.util.ValueStack;

@LoxiaTag(name="password", tldTagClass="loxia.struts2.taglib.tag.PasswordFieldTag", description="Render an HTML input field of type password")
public class PasswordField extends TextField {

	/**
     * The name of the default template for the Loxia NumberFieldTag
     */
	final public static String TEMPLATE = "loxia_password";
    
	public PasswordField(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected String getDefaultTemplate() {
		return TEMPLATE;
	}
}
