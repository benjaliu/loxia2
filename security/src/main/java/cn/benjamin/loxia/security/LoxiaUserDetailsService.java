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

import cn.benjamin.loxia.dao.UserDao;
import cn.benjamin.loxia.model.OperatingUnit;
import cn.benjamin.loxia.model.OperatingUnitType;
import cn.benjamin.loxia.model.Privilege;
import cn.benjamin.loxia.model.User;
import cn.benjamin.loxia.model.UserRole;
import cn.benjamin.loxia.utils.PropListCopyable;
import cn.benjamin.loxia.utils.PropertyUtil;

@Transactional
public class LoxiaUserDetailsService implements UserDetailsService {

	private UserDao userDao;	

	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		User user = userDao.findByLoginName(userName);
		if(user == null) throw new UsernameNotFoundException(userName + " is not existed.");
		LoxiaUserDetails result = new LoxiaUserDetails();
		User u = new User();
		OperatingUnit ou = new OperatingUnit();
		OperatingUnitType ouType = new OperatingUnitType();
		try {
			PropertyUtil.copyProperties(user.getOu().getType(), ouType);
			PropertyUtil.copyProperties(user.getOu(), ou, new PropListCopyable("id","code","name","isAvailable"));
			PropertyUtil.copyProperties(user, u, new PropListCopyable("id","loginName","userName","password",
					"isAccNonExpired","isAccNonLocked","isPwdNonExpired","isAvailable"));
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
