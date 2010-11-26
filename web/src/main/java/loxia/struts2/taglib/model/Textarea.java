/**
 * 
 * 
 * @author treacy
 * @date 2010-11-24
 */
package loxia.struts2.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

import loxia.struts2.taglib.annotation.LoxiaTag;
import loxia.struts2.taglib.annotation.LoxiaTagAttribute;

@LoxiaTag(name="textarea", tldTagClass="loxia.struts2.taglib.tag.TextareaTag", description="Render HTML textarea tag")
public class Textarea extends LoxiaClosingUIBean {

	/**
     * The name of the default template for the Loxia TextAreaTag
     */
    final public static String OPEN_TEMPLATE = "loxia_textarea";
    final public static String TEMPLATE = "loxia_textarea_close";
    
    protected String cols;
    protected String rows;
    protected String wrap;
    protected String readonly;
    protected String checkmaster;
    
    public Textarea(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
	public String getDefaultOpenTemplate() {
		return OPEN_TEMPLATE;
	}

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
    
    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (cols != null) {
            addParameter("cols", findString(cols));
        }

        if (rows != null) {
            addParameter("rows", findString(rows));
        }

        if (wrap != null) {
            addParameter("wrap", findString(wrap));
        }

        if (readonly != null) {
            addParameter("readonly", findValue(readonly, Boolean.class));
        }

		if(checkmaster != null){
			addParameter("checkmaster", findString(checkmaster));
		}
    }

    @LoxiaTagAttribute(description="HTML cols attribute", type="Integer")
	public void setCols(String cols) {
		this.cols = cols;
	}

    @LoxiaTagAttribute(description="HTML rows attribute", type="Integer")
	public void setRows(String rows) {
		this.rows = rows;
	}

    @LoxiaTagAttribute(description="HTML wrap attribute")
	public void setWrap(String wrap) {
		this.wrap = wrap;
	}

	@LoxiaTagAttribute(description="Whether the textarea is readonly", type="Boolean", defaultValue="false")
	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	@LoxiaTagAttribute(description="Attributes Extension for Client Validation", type="String")
	public void setCheckmaster(String checkmaster) {
		this.checkmaster = checkmaster;
	}

}
