package loxia.struts2.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import loxia.struts2.taglib.annotation.LoxiaTag;
import loxia.struts2.taglib.annotation.LoxiaTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

@LoxiaTag(name="number", tldTagClass="loxia.struts2.taglib.tag.NumberFieldTag", description="Render an HTML input field of type number")
public class NumberField extends TextField {

	/**
     * The name of the default template for the Loxia NumberFieldTag
     */
	final public static String TEMPLATE = "loxia_number";
	
	protected String decimal;
    protected String min;
    protected String max;
    
	public NumberField(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected String getDefaultTemplate() {
		return TEMPLATE;
	}
	
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		
		if(decimal != null){
			addParameter("decimal", findValue(decimal,Integer.class));
		}
		if(min != null){
			addParameter("min", findValue(min, Integer.class));
		}
		if(max != null){
			addParameter("max", findValue(max, Integer.class));
		}
	}
	
	@LoxiaTagAttribute(description="Attributes Extension for Decimal", type="Integer", defaultValue="0")
	public void setDecimal(String decimal) {
		this.decimal = decimal;
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
