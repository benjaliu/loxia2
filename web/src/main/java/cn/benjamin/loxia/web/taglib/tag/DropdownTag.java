package cn.benjamin.loxia.web.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import cn.benjamin.loxia.web.taglib.model.Dropdown;
import cn.benjamin.loxia.web.taglib.model.NumberField;

import com.opensymphony.xwork2.util.ValueStack;

public class DropdownTag extends TextFieldTag {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 5801392039734102013L;
	
	protected String editable;
    protected String choice;
    protected String findMode;
    
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new NumberField(stack,req,res);
	}
	
	protected void populateParams() {
        super.populateParams();
        Dropdown dropdown = ((Dropdown) component);
        dropdown.setEditable(editable);
        dropdown.setChoice(choice);
        dropdown.setFindMode(findMode);
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public void setFindMode(String findMode) {
		this.findMode = findMode;
	}	
	
}
