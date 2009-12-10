package cn.benjamin.loxia.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import cn.benjamin.loxia.dao.LoxiaUserDao;
import cn.benjamin.loxia.model.Privilege;
import cn.benjamin.loxia.model.User;
import cn.benjamin.loxia.model.UserRole;

@Transactional
public class LoxiaUserDetailsService implements UserDetailsService {

	private LoxiaUserDao userDao;	

	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		User user = userDao.findByLoginName(userName);
		if(user == null) return null;
		LoxiaUserDetails result = new LoxiaUserDetails();
		result.setUsername(user.getLoginName());
		result.setPassword(user.getPassword());
		result.setAccountNonExpired(user.getIsAccNonExpired());
		result.setAccountNonLocked(user.getIsAccNonLocked());
		result.setCredentialsNonExpired(user.getIsPwdNonExpired());
		result.setEnabled(user.getIsAvailable());
		
		List<LoxiaGrantedAuthority> authorities = new ArrayList<LoxiaGrantedAuthority>();
		Map<String,Set<Long>> map = new HashMap<String, Set<Long>>(); 
		for(UserRole ur: user.getUserRoles()){
			for(Privilege p : ur.getRole().getPrivileges()){
				Set<Long> ids = map.get(p.getAcl());
				if(ids == null){
					ids = new HashSet<Long>();
					map.put(p.getAcl(), ids);
				}
				ids.add(ur.getOu().getId());
			}
		}
		for(String key: map.keySet())
			authorities.add(new LoxiaGrantedAuthority(key,map.get(key)));
		
		result.setLoxiaAuthorities(authorities);
		return result;
	}
	
	public LoxiaUserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(LoxiaUserDao userDao) {
		this.userDao = userDao;
	}

}
