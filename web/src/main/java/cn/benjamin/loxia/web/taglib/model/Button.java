package cn.benjamin.loxia.web.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.benjamin.loxia.web.taglib.annotation.LoxiaTag;
import cn.benjamin.loxia.web.taglib.annotation.LoxiaTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

@LoxiaTag(name="button", tldTagClass="cn.benjamin.loxia.web.taglib.tag.ButtonTag", description="Render an HTML input field of type button")
public class Button extends LoxiaUIBean {

	/**
     * The name of the default template for the Loxia ButtonTag
     */
    final public static String TEMPLATE = "loxia_button";

    protected String buttonType;
    protected String href;
    protected String target;
    protected String popfor;
    

    protected String contextPath;

    public Button(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.contextPath = request.getContextPath();
    }

	protected String getDefaultTemplate() {
		return TEMPLATE;
	}

    @Override
    protected void evaluateExtraParams() {
		super.evaluateExtraParams();

		if(buttonType != null){
			addParameter("buttonType", findString(buttonType));
		}
		if(href != null){
			if(this.contextPath == null)
				this.contextPath = "";
			String tmpHref = findString(href);
			if(tmpHref.indexOf("/") == 0){
				addParameter("href", this.contextPath + tmpHref);
			}else{
				addParameter("href", tmpHref);
			}
		}
		if(target != null){
			addParameter("target", findString(target));
		}
		if(popfor != null){
			addParameter("popfor", findString(popfor));
		}
	}


	@LoxiaTagAttribute(description="Attributes Extension for Button Type", type="String", defaultValue="button")
	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

	@LoxiaTagAttribute(description="Attributes Extension for Button Type:pop Return Object Selector", type="String", defaultValue="null")
	public void setPopfor(String popfor) {
		this.popfor = popfor;
	}

	@LoxiaTagAttribute(description="Attributes Extension for Button Type:anchor Href Url", type="String", defaultValue="null")
	public void setHref(String href) {
		this.href = href;
	}

	@LoxiaTagAttribute(description="Attributes Extension for Button Type:anchor Target", type="String",defaultValue="_self")
	public void setTarget(String target) {
		this.target = target;
	}
}
