package cn.benjamin.loxia.struts2;

import cn.benjamin.loxia.security.LoxiaUserDetails;

public interface LoxiaUserDetailsAware {
	void setLoxiaUserDetails(LoxiaUserDetails userDetails);
}
