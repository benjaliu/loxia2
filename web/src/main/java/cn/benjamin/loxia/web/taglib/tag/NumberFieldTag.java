package cn.benjamin.loxia.web.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import cn.benjamin.loxia.web.taglib.model.NumberField;

import com.opensymphony.xwork2.util.ValueStack;

public class NumberFieldTag extends TextFieldTag {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 5801392039734102013L;
	
	protected String decimal;
    protected String min;
    protected String max;
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new NumberField(stack,req,res);
	}
	
	protected void populateParams() {
        super.populateParams();
        NumberField numberField = ((NumberField) component);
        numberField.setDecimal(decimal);
        numberField.setMin(min);
        numberField.setMax(max);
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public void setMax(String max) {
		this.max = max;
	}
	
}
