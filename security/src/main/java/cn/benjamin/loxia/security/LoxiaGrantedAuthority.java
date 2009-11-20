package cn.benjamin.loxia.security;

import org.springframework.security.GrantedAuthority;

public class LoxiaGrantedAuthority implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 856242394713864837L;

	public String getAuthority() {
		// TODO Auto-generated method stub
		return null;
	}

	public int compareTo(Object o) {
		if(o ==null) throw new IllegalArgumentException();
		if(!(o instanceof LoxiaGrantedAuthority))
			throw new IllegalArgumentException();
		LoxiaGrantedAuthority auth = (LoxiaGrantedAuthority)o;
		return getAuthority().compareTo(auth.getAuthority());
	}

}
