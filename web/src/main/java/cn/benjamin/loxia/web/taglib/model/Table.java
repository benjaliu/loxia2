package cn.benjamin.loxia.web.taglib.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.benjamin.loxia.web.taglib.annotation.LoxiaTag;
import cn.benjamin.loxia.web.taglib.annotation.LoxiaTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

@LoxiaTag(name="table", tldTagClass="cn.benjamin.loxia.web.taglib.tag.TableTag", description="Render an HTML Table element")
public class Table extends LoxiaClosingUIBean {
	
	/**
     * The name of the default template for the Loxia TableTag
     */
	final public static String OPEN_TEMPLATE = "loxia_table";
    final public static String TEMPLATE = "loxia_table_close";
    
    protected String settings;
    protected String cellpadding;
    protected String cellspacing;
    
	public Table(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
	@Override
	public String getDefaultOpenTemplate() {
		return OPEN_TEMPLATE;
	}

	@Override
	protected String getDefaultTemplate() {
		return TEMPLATE;
	}
	
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		
		if(settings != null){
			addParameter("settings", findString(settings));
		}
	}

	@LoxiaTagAttribute(description="Table Settings", type="String")
	public void setSettings(String settings) {
		this.settings = settings;
	}
	
	@LoxiaTagAttribute(description="Table's Cellpadding", type="Integer", defaultValue="0")
	public void setCellpadding(String cellpadding) {
		this.cellpadding = cellpadding;
	}

	@LoxiaTagAttribute(description="Table's Cellspacing", type="Integer", defaultValue="0")
	public void setCellspacing(String cellspacing) {
		this.cellspacing = cellspacing;
	}

}
