package loxia.struts2;

import loxia.security.LoxiaUserDetails;
import loxia.model.OperatingUnit;
import loxia.model.User;

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
