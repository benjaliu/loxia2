package cn.benjamin.loxia.web;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

public class BaseAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4113598733962053745L;
	
	protected static final Logger logger = LoggerFactory.getLogger(BaseAction.class);
	
	public static final String FOLLOWING_URL_AFTER_OPERATING_UNIT_PICKUP = "BaseAction.followingUrl";

	public static final String JSON = "json";
	
	private String acl;
	private Long selectedOuId;	
	
	protected int pageSize;
	protected int currentPage;
	protected String sortString;	

	protected String getMessage(String key, Object[] args){
		Locale locale = ActionContext.getContext().getLocale();
		return LocalizedTextUtil.findText(this.getClass(), key, locale, key, args);
	}
	
	public void addActionError(String errKey, Object[] args){
		addActionError(getMessage(errKey, args));
	}

	public void addFieldError(String fieldName, String errKey, Object[] args){
		addFieldError(fieldName, getMessage(errKey, args));
	}

	public void addActionMessage(String msgKey, Object[] args){
		addActionMessage(getMessage(msgKey, args));
	}
	
	public String getAcl() {
		return acl;
	}
	public void setAcl(String acl) {
		this.acl = acl;
	}
	public Long getSelectedOuId() {
		return selectedOuId;
	}
	public void setSelectedOuId(Long selectedOuId) {
		this.selectedOuId = selectedOuId;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getSortString() {
		return sortString;
	}

	public void setSortString(String sortString) {
		this.sortString = sortString;
	}
}
