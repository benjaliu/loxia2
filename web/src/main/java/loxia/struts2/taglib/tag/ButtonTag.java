package loxia.struts2.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import loxia.struts2.taglib.model.Button;

import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

public class ButtonTag extends LoxiaAbstractUITag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8184855598380407170L;
	
	protected String buttonType;
    protected String href;
    protected String target;
    protected String popfor;

    @Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Button(stack,req,res);
	}
	
    protected void populateParams() {
        super.populateParams();
        Button button = (Button)component;
        button.setButtonType(buttonType);
        button.setHref(href);
        button.setTarget(target);
        button.setPopfor(popfor);
    }

	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setPopfor(String popfor) {
		this.popfor = popfor;
	}

}
