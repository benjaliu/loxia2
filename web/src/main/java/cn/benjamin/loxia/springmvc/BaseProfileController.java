package cn.benjamin.loxia.springmvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.benjamin.loxia.model.OperatingUnit;
import cn.benjamin.loxia.model.User;
import cn.benjamin.loxia.security.LoxiaUserDetails;
import cn.benjamin.loxia.struts2.LoxiaUserDetailsAware;

public class BaseProfileController implements LoxiaUserDetailsAware {
	
	protected Logger logger = LoggerFactory.getLogger(BaseProfileController.class);
	
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
