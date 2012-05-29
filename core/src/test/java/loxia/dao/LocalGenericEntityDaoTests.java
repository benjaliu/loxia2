package loxia.dao;

import java.util.Arrays;
import java.util.List;

import loxia.dao.Pagination;
import loxia.dao.Sort;
import loxia.model.InnerUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;


@ContextConfiguration(locations={"classpath*:loxia-service-*.xml",
		"classpath*:loxia-hibernate-*.xml",
		"classpath*:loxia-test-hibernate-*.xml",
		"classpath*:loxia-test-dynamic-*.xml"})
public class LocalGenericEntityDaoTests extends AbstractTestNGSpringContextTests {
	
	@Autowired
	private InnerUserDao userDao;
	
	@Test
	public void testUserDaoAdd(){
		System.out.println(Arrays.asList(userDao.getClass().getInterfaces()));
		InnerUser user = new InnerUser();
		user.setId(1l);
		user.setLoginName("user");
		user.setPassword("loxia");
		user.setUserName("Loxia User");
		userDao.save(user);
		System.out.println("==============================0");
		InnerUser u = userDao.getByPrimaryKey(1l);
		assert u != null : "user is null";
		assert u.getLoginName().equals("user") : "wrong user";
		System.out.println("==============================1");
		u.setUserName("Dragon");
		userDao.save(u);
		System.out.println("==============================2");
		u = userDao.getByPrimaryKey(1l);
		assert u.getUserName().equals("Dragon") : "wrong user name";
		userDao.findUserCount();
		System.out.println("==============================3");
		user = new InnerUser();
		user.setId(1l);
		user.setLoginName("user");
		user.setPassword("loxia");
		user.setUserName("Loxia User");
		userDao.save(user);
		System.out.println("==============================4");
		List<InnerUser> users = userDao.findUsers(new Sort[]{new Sort("u.userName")});		
		u = userDao.findUserByLoginName("user");
		Pagination<InnerUser> p = userDao.findByLoginNameP(0, 20, "user");
		System.out.println(p.getCount());
		System.out.println(users.size());
		System.out.println(u.getUserName());
		List<InnerUser> anoUsers = userDao.findUserByName(0, 20, "U", new Sort[]{new Sort("u.userName")});
		System.out.println(anoUsers.size());
		System.out.println(userDao.findByLoginName(0, 20, null).size());
		System.out.println(userDao.findByLoginName(0, 20, "a").size());
		userDao.findByLoginNameSql(0, 20, "u", null, null);
		userDao.findByLoginNameSql(0, 20, "u", null, "w");	
		users = userDao.findByLoginNameSql1(0, 20, null, null, null);
		System.out.println(users.iterator().next().getUserName());
		userDao.executeDDL("shutdown");
	}
}
