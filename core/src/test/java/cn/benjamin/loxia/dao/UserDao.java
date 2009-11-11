package cn.benjamin.loxia.dao;

import java.util.List;

import cn.benjamin.loxia.annotation.Query;
import cn.benjamin.loxia.annotation.QueryParam;
import cn.benjamin.loxia.model.User;


public interface UserDao extends GenericEntityDao<User ,Long> {
	
	@Query
	List<User> findUsers();
	
	@Query
	User findUserByLoginName(@QueryParam("loginName") String loginName);
}
