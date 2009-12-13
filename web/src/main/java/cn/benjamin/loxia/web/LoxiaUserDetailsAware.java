package cn.benjamin.loxia.web;

import cn.benjamin.loxia.security.LoxiaUserDetails;

public interface LoxiaUserDetailsAware {
	void setLoxiaUserDetails(LoxiaUserDetails userDetails);
}
