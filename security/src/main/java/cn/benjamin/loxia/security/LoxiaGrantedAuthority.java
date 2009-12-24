package cn.benjamin.loxia.security;

import java.util.Set;

import org.springframework.security.GrantedAuthority;

public class LoxiaGrantedAuthority implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 856242394713864837L;
	
	public LoxiaGrantedAuthority() {
	}
	
	public LoxiaGrantedAuthority(String authority) {
		this.authority = authority;
	}

	public LoxiaGrantedAuthority(String authority, Set<Long> ouIds) {
		super();
		this.authority = authority;
		this.ouIds = ouIds;
	}

	private String authority;
	private Set<Long> ouIds;
	
	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	public Set<Long> getOuIds() {
		return ouIds;
	}

	public void setOuIds(Set<Long> ouIds) {
		this.ouIds = ouIds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authority == null) ? 0 : authority.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoxiaGrantedAuthority other = (LoxiaGrantedAuthority) obj;
		if (authority == null) {
			if (other.authority != null)
				return false;
		} else if (!authority.equals(other.authority))
			return false;
		return true;
	}	

	public int compareTo(Object o) {
		if(o ==null) throw new IllegalArgumentException();
		if(!(o instanceof LoxiaGrantedAuthority))
			throw new IllegalArgumentException();
		LoxiaGrantedAuthority auth = (LoxiaGrantedAuthority)o;
		return getAuthority().compareTo(auth.getAuthority());
	}

	@Override
	public String toString() {
		return "LoxiaGrantedAuthority [authority=" + authority + ", ouIds="
				+ ouIds + "]";
	}
}
