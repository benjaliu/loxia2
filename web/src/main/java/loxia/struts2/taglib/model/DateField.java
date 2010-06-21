package loxia.struts2.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import loxia.struts2.taglib.annotation.LoxiaTag;
import loxia.struts2.taglib.annotation.LoxiaTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

@LoxiaTag(name="date", tldTagClass="loxia.struts2.taglib.tag.DateFieldTag", description="Render an HTML input field of type date")
public class DateField extends TextField {

	/**
     * The name of the default template for the Loxia NumberFieldTag
     */
	final public static String TEMPLATE = "loxia_date";
	
    protected String min;
    protected String max;
    
	public DateField(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected String getDefaultTemplate() {
		return TEMPLATE;
	}
	
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		
		if(min != null){
			addParameter("min", findValue(min, String.class));
		}
		if(max != null){
			addParameter("max", findValue(max, String.class));
		}
	}	

	@LoxiaTagAttribute(description="Attributes Extension for Min", type="Ineteger")
	public void setMin(String min) {
		this.min = min;
	}

	@LoxiaTagAttribute(description="Attributes Extension for Max", type="Ineteger")
	public void setMax(String max) {
		this.max = max;
	}
}
