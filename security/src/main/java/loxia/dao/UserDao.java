package loxia.dao;

import org.springframework.transaction.annotation.Transactional;

import loxia.annotation.Query;
import loxia.annotation.QueryParam;
import loxia.model.User;

@Transactional
public interface UserDao extends GenericEntityDao<User, Long> {

	@Transactional(readOnly=true)
	@Query(value="select u from loxia.model.User u where u.loginName = :loginName")	
	User findByLoginName(@QueryParam("loginName") String loginName);
}
