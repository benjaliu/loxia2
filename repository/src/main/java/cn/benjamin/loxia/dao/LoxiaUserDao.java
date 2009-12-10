package cn.benjamin.loxia.dao;

import org.springframework.transaction.annotation.Transactional;

import cn.benjamin.loxia.annotation.Query;
import cn.benjamin.loxia.annotation.QueryParam;
import cn.benjamin.loxia.model.User;

@Transactional
public interface LoxiaUserDao extends GenericEntityDao<User, Long> {

	@Query(value="select u from cn.benjamin.loxia.model.User u where u.loginName = :loginName")
	User findByLoginName(@QueryParam("loginName") String loginName);
}
