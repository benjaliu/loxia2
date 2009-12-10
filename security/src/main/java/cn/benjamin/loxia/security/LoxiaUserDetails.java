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
	
	private String username;
	private String password;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	
	private List<LoxiaGrantedAuthority> loxiaAuthorities = new ArrayList<LoxiaGrantedAuthority>();	

	public GrantedAuthority[] getAuthorities() {
		return (GrantedAuthority[])loxiaAuthorities.toArray();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<LoxiaGrantedAuthority> getLoxiaAuthorities() {
		return loxiaAuthorities;
	}
	public void setLoxiaAuthorities(List<LoxiaGrantedAuthority> loxiaAuthorities) {
		this.loxiaAuthorities = loxiaAuthorities;
	}
}
