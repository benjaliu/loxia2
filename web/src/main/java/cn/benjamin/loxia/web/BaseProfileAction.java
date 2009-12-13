package cn.benjamin.loxia.web;

import cn.benjamin.loxia.security.LoxiaUserDetails;

public class BaseProfileAction extends BaseAction implements
		LoxiaUserDetailsAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2262288481658446012L;
	
	protected LoxiaUserDetails userDetails;
	
	public void setLoxiaUserDetails(LoxiaUserDetails userDetails) {
		this.userDetails = userDetails;

	}

}
