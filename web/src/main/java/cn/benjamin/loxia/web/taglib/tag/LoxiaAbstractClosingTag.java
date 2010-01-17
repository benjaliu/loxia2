package cn.benjamin.loxia.web.taglib.tag;

import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import cn.benjamin.loxia.web.taglib.model.LoxiaClosingUIBean;

public abstract class LoxiaAbstractClosingTag extends AbstractClosingTag {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8705764936208971519L;
	
	protected String htmlAttr;
    
	@Override
    protected void populateParams() {
        super.populateParams();

        LoxiaClosingUIBean uiBean = (LoxiaClosingUIBean) component;
        uiBean.setHtmlAttr(htmlAttr);
    }

	public void setHtmlAttr(String htmlAttr) {
		this.htmlAttr = htmlAttr;
	}
}
