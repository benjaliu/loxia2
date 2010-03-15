package cn.benjamin.loxia.struts2;

import cn.benjamin.loxia.model.OperatingUnit;
import cn.benjamin.loxia.model.User;
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

	public User getCurrentUser(){
		if(this.userDetails == null) return null;
		return this.userDetails.getUser();
	}
	
	public OperatingUnit getCurrentOperatingUnit(){
		if(this.userDetails == null) return null;
		return this.userDetails.getCurrentOu();
	}
	
	public boolean checkPrivilege(String[] acls){
		if(this.userDetails == null) return false;		
		return this.userDetails.checkAuthority(acls);
	}
}
