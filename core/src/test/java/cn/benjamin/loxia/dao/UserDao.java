package cn.benjamin.loxia.dao;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.benjamin.loxia.annotation.DynamicQuery;
import cn.benjamin.loxia.annotation.NamedQuery;
import cn.benjamin.loxia.annotation.NativeQuery;
import cn.benjamin.loxia.annotation.Query;
import cn.benjamin.loxia.annotation.QueryParam;
import cn.benjamin.loxia.model.User;

@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
public interface UserDao extends GenericEntityDao<User ,Long> {
	
	@NamedQuery
	List<User> findUsers(Sort[] sorts);
	
	@NamedQuery
	User findUserByLoginName(@QueryParam("loginName") String loginName);
	
	@Query(value="select u from User u where u.userName like '%' + :userName + '%'", pagable=true)
	List<User> findUserByName(int start, int pageSize, @QueryParam("userName") String userName, Sort[] sorts);
	
	@DynamicQuery(pagable = true)
	List<User> findByLoginName(int start, int pageSize, @QueryParam("loginName") String loginName);
	
	@NativeQuery(sqlResultMapping="user", pagable=true)
	List<Object[]> findByLoginNameSql(int start, int pageSize, 
			@QueryParam("loginName") String loginName, 
			@QueryParam("userName") String userName, 
			@QueryParam("password") String password);
}
