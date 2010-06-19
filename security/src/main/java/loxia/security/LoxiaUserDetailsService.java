package loxia.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loxia.dao.UserDao;
import loxia.model.OperatingUnit;
import loxia.model.OperatingUnitType;
import loxia.model.Privilege;
import loxia.model.User;
import loxia.model.UserRole;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LoxiaUserDetailsService implements UserDetailsService {

	private UserDao userDao;	

	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		User user = userDao.findByLoginName(userName);
		if(user == null) throw new UsernameNotFoundException(userName + " is not existed.");
		LoxiaUserDetails result = new LoxiaUserDetails();
		User u = new User();
		OperatingUnit ou = new OperatingUnit();
		OperatingUnitType ouType = new OperatingUnitType();
		try {
			PropertyUtils.copyProperties(user.getOu().getType(), ouType);
			ou.setId(user.getOu().getId());
			ou.setCode(user.getOu().getCode());
			ou.setName(user.getOu().getName());
			ou.setIsAvailable(user.getOu().getIsAvailable());
			u.setId(user.getId());
			u.setLoginName(user.getLoginName());
			u.setUserName(user.getUserName());
			u.setPassword(user.getPassword());
			u.setIsAccNonExpired(user.getIsAccNonExpired());
			u.setIsAccNonLocked(user.getIsAccNonLocked());
			u.setIsPwdNonExpired(user.getIsPwdNonExpired());
			u.setIsAvailable(user.getIsAvailable());
			u.setIsSystem(user.getIsSystem());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Copy bean values error.");
		} 
		ou.setType(ouType);
		u.setOu(ou);
		result.setUser(u);
		
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
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
