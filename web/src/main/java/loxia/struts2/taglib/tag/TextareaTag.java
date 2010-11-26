/**
 * 
 * 
 * @author treacy
 * @date 2010-11-24
 */
package loxia.struts2.taglib.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import loxia.struts2.taglib.model.Textarea;

import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

public class TextareaTag extends LoxiaAbstractClosingTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7338471595087672583L;
	
	protected String cols;
    protected String rows;
    protected String wrap;
    protected String readonly;
    protected String checkmaster;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Textarea(stack, req, res);
	}

	protected void populateParams() {
        super.populateParams();
        Textarea textarea = (Textarea)component;
        textarea.setCols(cols);
        textarea.setRows(rows);
        textarea.setWrap(wrap);
        textarea.setReadonly(readonly);
        textarea.setCheckmaster(checkmaster);
    }

	public void setCols(String cols) {
		this.cols = cols;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public void setWrap(String wrap) {
		this.wrap = wrap;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public void setCheckmaster(String checkmaster) {
		this.checkmaster = checkmaster;
	}
}
