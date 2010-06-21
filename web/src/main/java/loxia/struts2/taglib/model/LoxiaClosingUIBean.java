package loxia.struts2.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.components.Form;

import loxia.struts2.taglib.annotation.LoxiaTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

public abstract class LoxiaClosingUIBean extends ClosingUIBean {
	
	protected String htmlAttr;
	
	protected LoxiaClosingUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

	@Override
	protected void populateComponentHtmlId(Form form) {
		String tryId;
        if (id != null) {
            // this check is needed for backwards compatibility with 2.1.x
            if (altSyntax()) {
                tryId = findString(id);
            } else {
                tryId = id;
            }
            addParameter("id", tryId);
            addParameter("escapedId", escape(tryId));
        }
	}	
	
	@Override
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		if(htmlAttr != null){
			addParameter("htmlAttr", findString(htmlAttr));
		}
	}
	
	@LoxiaTagAttribute(description="Attributes Extension", type="String")
	public void setHtmlAttr(String htmlAttr) {
		this.htmlAttr = htmlAttr;
	}
}
