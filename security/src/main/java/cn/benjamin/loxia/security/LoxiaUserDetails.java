package cn.benjamin.loxia.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

public class LoxiaUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7151706265719401726L;
	
	private List<LoxiaGrantedAuthority> loxiaAuthorities = new ArrayList<LoxiaGrantedAuthority>();	

	public GrantedAuthority[] getAuthorities() {
		return (GrantedAuthority[])loxiaAuthorities.toArray();
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<LoxiaGrantedAuthority> getLoxiaAuthorities() {
		return loxiaAuthorities;
	}
	public void setLoxiaAuthorities(List<LoxiaGrantedAuthority> loxiaAuthorities) {
		this.loxiaAuthorities = loxiaAuthorities;
	}
}
