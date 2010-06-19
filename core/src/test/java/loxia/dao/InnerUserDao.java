package loxia.dao;

import java.util.List;

import loxia.annotation.DynamicQuery;
import loxia.annotation.NamedQuery;
import loxia.annotation.NativeQuery;
import loxia.annotation.Query;
import loxia.annotation.QueryParam;
import loxia.dao.GenericEntityDao;
import loxia.dao.Pagination;
import loxia.dao.Sort;
import loxia.model.InnerUser;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
public interface InnerUserDao extends GenericEntityDao<InnerUser ,Long> {
	
	@NamedQuery
	List<InnerUser> findUsers(Sort[] sorts);
	
	@NamedQuery
	InnerUser findUserByLoginName(@QueryParam("loginName") String loginName);
	
	@Query(value="select u from InnerUser u where u.userName like '%' + :userName + '%'", pagable=true)
	List<InnerUser> findUserByName(int start, int pageSize, @QueryParam("userName") String userName, Sort[] sorts);
	
	@Query("select count(u) from InnerUser u")
	long findUserCount();
	
	@DynamicQuery(pagable = true)
	List<InnerUser> findByLoginName(int start, int pageSize, @QueryParam("loginName") String loginName);
	
	@DynamicQuery(value="InnerUser.findByLoginName", pagable = true)
	Pagination<InnerUser> findByLoginNameP(int start, int pageSize, @QueryParam("loginName") String loginName);
	
	@NativeQuery(pagable=true, alias="userName", clazzes=String.class)
	List<String> findByLoginNameSql(int start, int pageSize, 
			@QueryParam("loginName") String loginName, 
			@QueryParam("userName") String userName, 
			@QueryParam("password") String password);
}
