package cn.benjamin.loxia.struts2.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.benjamin.loxia.struts2.taglib.annotation.LoxiaTag;
import cn.benjamin.loxia.struts2.taglib.annotation.LoxiaTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

@LoxiaTag(name="dropdown", tldTagClass="cn.benjamin.loxia.struts2.taglib.tag.DropdownTag", description="Render an HTML input field of type dropdown")
public class Dropdown extends TextField {

	/**
     * The name of the default template for the Loxia TextFieldTag
     */
    final public static String TEMPLATE = "loxia_dropdown";

    protected String editable;
    protected String choice;
    protected String findMode;
    
    public Dropdown(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
	
	@Override
	protected String getDefaultTemplate() {
		return TEMPLATE;
	}
	
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		
		if (editable != null) {
            addParameter("editable", findValue(editable,Boolean.class));
        }

        if (choice != null) {
            addParameter("choice", findString(choice));
        }
		
		if(findMode != null){
			addParameter("findMode", findString(findMode));
		}
	}

	@LoxiaTagAttribute(description="Attributes Extension for Editable setting", type="Boolean", defaultValue="false")
	public void setEditable(String editable) {
		this.editable = editable;
	}

	@LoxiaTagAttribute(description="Data source for list", type="String")
	public void setChoice(String choice) {
		this.choice = choice;
	}

	@LoxiaTagAttribute(description="Match mode, can be 'like','leftlike', and 'rightlike'", type="String", defaultValue="like")
	public void setFindMode(String findMode) {
		this.findMode = findMode;
	}
	
}
