package loxia.struts2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.RequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

public class BaseAction extends ActionSupport implements RequestAware, ParameterAware{

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
	
	protected Map<String, Object> request;
	protected Map<String, String[]> parameters;

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}
	
	public void setParameters(Map<String, String[]> parameters) {
		this.parameters = parameters;
	}
	
	protected String[] getProperties(String objName,String... exclusions){
		if(parameters == null) return new String[0];
		Set<String> resultSet = new HashSet<String>();
		Set<String> exclusionSet = new HashSet<String>();
		if(exclusions != null)
			exclusionSet.addAll(Arrays.asList(exclusions));
		String pattern = objName + '.';
		for(String key: parameters.keySet()){
			if(key.startsWith(pattern)){
				String v = key.substring(pattern.length());
				int delim = v.indexOf('.');
				v = delim < 0? v : v.substring(0,delim);
				if(!exclusionSet.contains(v))
					resultSet.add(v);
			}
		}
		return resultSet.toArray(new String[0]);
	}

	protected String getMessage(String key, Object... args){
		Locale locale = ActionContext.getContext().getLocale();
		return LocalizedTextUtil.findText(this.getClass(), key, locale, key, args);
	}
	
	public void addActionError(String errKey, Object... args){
		addActionError(getMessage(errKey, args));
	}

	public void addFieldError(String fieldName, String errKey, Object... args){
		addFieldError(fieldName, getMessage(errKey, args));
	}

	public void addActionMessage(String msgKey, Object... args){
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
