package cn.benjamin.loxia.struts2.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.benjamin.loxia.struts2.taglib.annotation.LoxiaTag;
import cn.benjamin.loxia.struts2.taglib.annotation.LoxiaTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

@LoxiaTag(name="textfield", tldTagClass="cn.benjamin.loxia.struts2.taglib.tag.TextFieldTag", description="Render an HTML input field of type text")
public class TextField extends LoxiaUIBean {

	/**
     * The name of the default template for the Loxia TextFieldTag
     */
    final public static String TEMPLATE = "loxia_text";

    protected String readonly;
    protected String checkmaster;
    protected String maxlength;
    protected String size;
    
    public TextField(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
	
	@Override
	protected String getDefaultTemplate() {
		return TEMPLATE;
	}
	
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		
		if (size != null) {
            addParameter("size", findString(size));
        }

        if (maxlength != null) {
            addParameter("maxlength", findString(maxlength));
        }
		
		if(readonly != null){
			addParameter("readonly", findValue(readonly,Boolean.class));
		}
		if(htmlAttr != null){
			addParameter("htmlAttr", findString(htmlAttr));
		}
		if(checkmaster != null){
			addParameter("checkmaster", findString(checkmaster));
		}
	}

	@LoxiaTagAttribute(description="Attributes Extension", type="String")
	public void setHtmlAttr(String htmlAttr) {
		this.htmlAttr = htmlAttr;
	}

	@LoxiaTagAttribute(description="Attributes Extension for Client Validation", type="String")
	public void setCheckmaster(String checkmaster) {
		this.checkmaster = checkmaster;
	}

	@LoxiaTagAttribute(description="Attributes Extension for ReadOnly", type="Boolean", defaultValue="false")
	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	@LoxiaTagAttribute(description="HTML maxlength attribute", type="Integer")
	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	@LoxiaTagAttribute(description="HTML size attribute",  type="Integer")
	public void setSize(String size) {
		this.size = size;
	}
	
}
