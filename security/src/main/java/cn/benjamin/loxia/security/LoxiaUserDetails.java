package cn.benjamin.loxia.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import cn.benjamin.loxia.model.OperatingUnit;
import cn.benjamin.loxia.model.User;

public class LoxiaUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7151706265719401726L;
	
	private User user;
	private OperatingUnit currentOu;
	
	private List<LoxiaGrantedAuthority> loxiaAuthorities = new ArrayList<LoxiaGrantedAuthority>();	
	
	private LoxiaGrantedAuthority currentAuthority;	

	public GrantedAuthority[] getAuthorities() {
		return loxiaAuthorities.toArray(new GrantedAuthority[]{});
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public OperatingUnit getCurrentOu() {
		return currentOu;
	}

	public void setCurrentOu(OperatingUnit currentOu) {
		this.currentOu = currentOu;
	}

	public String getUsername() {
		if(getUser()== null) return null;
		return getUser().getLoginName();
	}

	public String getPassword() {
		if(getUser()== null) return null;
		return getUser().getPassword();
	}

	public boolean isAccountNonExpired() {
		if(getUser()== null) return false;
		return getUser().getIsAccNonExpired();
	}

	public boolean isAccountNonLocked() {
		if(getUser()== null) return false;
		return getUser().getIsAccNonLocked();
	}

	public boolean isCredentialsNonExpired() {
		if(getUser()== null) return false;
		return getUser().getIsPwdNonExpired();
	}

	public boolean isEnabled() {
		if(getUser()== null) return false;
		return getUser().getIsAvailable();
	}

	public List<LoxiaGrantedAuthority> getLoxiaAuthorities() {
		return loxiaAuthorities;
	}
	public void setLoxiaAuthorities(List<LoxiaGrantedAuthority> loxiaAuthorities) {
		this.loxiaAuthorities = loxiaAuthorities;
	}
	
	public LoxiaGrantedAuthority getCurrentAuthority() {
		return currentAuthority;
	}

	public void setCurrentAuthority(LoxiaGrantedAuthority currentAuthority) {
		this.currentAuthority = currentAuthority;
	}
	
	public boolean checkAuthority(String[] acls){
		if(acls == null || getCurrentOu() == null) return false;
		Set<Long> ouIds = new HashSet<Long>();
		List<String> aclList = Arrays.asList(acls);
		for(LoxiaGrantedAuthority auth: getLoxiaAuthorities()){
			if(aclList.contains(auth.getAuthority()))
				ouIds.addAll(auth.getOuIds());
		}
		return ouIds.contains(getCurrentOu().getId());
	}
}
