package cn.benjamin.loxia.web.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.benjamin.loxia.web.taglib.model.Select;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractRequiredListTag;

import com.opensymphony.xwork2.util.ValueStack;

public class SelectTag extends AbstractRequiredListTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2072981059125531629L;
	
	protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String checkmaster;
    protected String htmlAttr;
    protected String size;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Select(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Select select = ((Select) component);
        select.setEmptyOption(emptyOption);
        select.setHeaderKey(headerKey);
        select.setHeaderValue(headerValue);
        select.setMultiple(multiple);
        select.setCheckmaster(checkmaster);
        select.setHtmlAttr(htmlAttr);
        select.setSize(size);
    }

    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }
    
	public void setHtmlAttr(String htmlAttr) {
		this.htmlAttr = htmlAttr;
	}

	public void setCheckmaster(String checkmaster) {
		this.checkmaster = checkmaster;
	}

	public void setSize(String size) {
		this.size = size;
	}
}
