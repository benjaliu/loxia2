package loxia.struts2.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import loxia.struts2.taglib.model.EditTable;

import com.opensymphony.xwork2.util.ValueStack;

public class EditTableTag extends LoxiaAbstractClosingTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8099901188906859982L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		// TODO Auto-generated method stub
		return new EditTable(stack,req,res);
	}
}
