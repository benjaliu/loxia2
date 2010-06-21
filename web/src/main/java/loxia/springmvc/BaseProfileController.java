package loxia.springmvc;

import loxia.security.LoxiaUserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loxia.model.OperatingUnit;
import loxia.model.User;
import loxia.struts2.LoxiaUserDetailsAware;

public class BaseProfileController extends AbstractController implements LoxiaUserDetailsAware {
	
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
	
	public void setCurrentOperatingUnit(OperatingUnit currentOu){
		this.userDetails.setCurrentOu(currentOu);
	}
	
	public boolean checkPrivilege(String[] acls){
		if(this.userDetails == null) return false;		
		return this.userDetails.checkAuthority(acls);
	}
}
