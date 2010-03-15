package cn.benjamin.loxia.struts2.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.benjamin.loxia.struts2.taglib.model.Table;

import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

public class TableTag extends LoxiaAbstractClosingTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7979803977773287600L;
	
	protected String settings;
    protected String cellpadding;
    protected String cellspacing;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		// TODO Auto-generated method stub
		return new Table(stack,req,res);
	}
	
	protected void populateParams() {
        super.populateParams();
        Table table = ((Table) component);
        table.setSettings(settings);
        table.setCellpadding(cellpadding);
        table.setCellspacing(cellspacing);
	}

	public void setSettings(String settings) {
		this.settings = settings;
	}

	public void setCellpadding(String cellpadding) {
		this.cellpadding = cellpadding;
	}

	public void setCellspacing(String cellspacing) {
		this.cellspacing = cellspacing;
	}

}
