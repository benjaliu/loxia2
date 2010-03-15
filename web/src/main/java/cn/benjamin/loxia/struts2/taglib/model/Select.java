package cn.benjamin.loxia.struts2.taglib.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Form;
import org.apache.struts2.components.ListUIBean;

import cn.benjamin.loxia.struts2.taglib.annotation.LoxiaTag;
import cn.benjamin.loxia.struts2.taglib.annotation.LoxiaTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

@LoxiaTag(name="select", tldTagClass="cn.benjamin.loxia.struts2.taglib.tag.SelectTag", description="Render an select element")
public class Select extends ListUIBean {

	/**
     * The name of the default template for the Loxia DataSelectTag
     */
    final public static String TEMPLATE = "loxia_select";
    
    protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String checkmaster;
    protected String htmlAttr;
    protected String size;

    public Select(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    @Override
	protected String getDefaultTemplate() {
		return TEMPLATE;
	}

    @SuppressWarnings("unchecked")
	public void evaluateExtraParams() {
    	if(list == null || (list != null && list instanceof String &&
    			((String)list).trim().length() == 0)){
    		list = new ArrayList();	    	
    	}
    	
        super.evaluateExtraParams();


        if (emptyOption != null) {
            addParameter("emptyOption", findValue(emptyOption, Boolean.class));
        }
        
        if (multiple != null) {
            addParameter("multiple", findValue(multiple, Boolean.class));
        }

        if ((headerKey != null) && (headerValue != null)) {
            addParameter("headerKey", findString(headerKey));
            addParameter("headerValue", findString(headerValue));
        }
        if (size != null) {
            addParameter("size", findString(size));
        }
		if(htmlAttr != null){
			addParameter("htmlAttr", findString(htmlAttr));
		}
		if(checkmaster != null){
			addParameter("checkmaster", findString(checkmaster));
		}
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
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object obj1, Object obj2) {
		boolean flag = super.contains(obj1, obj2);
		if (!flag){
			if (obj1 != null){
				if ( !(obj1 instanceof Map)
						&& !(obj1 instanceof Collection) 
						&& !(obj1.getClass().isArray())){
					return obj1.toString().equals(obj2.toString());					
				}
			}
		}
		return flag;
	}
	
	@LoxiaTagAttribute(description="Whether or not to add an empty (--) option after the header option", type="Boolean", defaultValue="false")
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    @Override
    @LoxiaTagAttribute(description="Iterable source to populate from. If the list is a Map (key, value), the Map key will become the option 'value'" +
            " parameter and the Map value will become the option body.")
	public void setList(Object list) {
		super.setList(list);
	}

    @LoxiaTagAttribute(description=" Key for first item in list. Must not be empty! '-1' and '' is correct, '' is bad.")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    @LoxiaTagAttribute(description="Value expression for first item in list")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @LoxiaTagAttribute(description=" Creates a multiple select. The tag will pre-select multiple values" +
                " if the values are passed as an Array (of appropriate types) via the value attribute. Passing " +
                "a Collection may work too? Haven't tested this.", type="Boolean", defaultValue="false")
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }
    
    @LoxiaTagAttribute(description="Attributes Extension", type="String")
	public void setHtmlAttr(String htmlAttr) {
		this.htmlAttr = htmlAttr;
	}
    
    @LoxiaTagAttribute(description="Attributes Extension for Client Validation", type="String")
	public void setCheckmaster(String checkmaster) {
		this.checkmaster = checkmaster;
	}
    
    @LoxiaTagAttribute(description="Size of the element box (# of elements to show)", type="Integer")
    public void setSize(String size) {
        this.size = size;
    }
}
