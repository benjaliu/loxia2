package loxia.struts2;

import loxia.security.LoxiaUserDetails;

public interface LoxiaUserDetailsAware {
	void setLoxiaUserDetails(LoxiaUserDetails userDetails);
}
