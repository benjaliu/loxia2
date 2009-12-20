package cn.benjamin.loxia.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import cn.benjamin.loxia.model.InnerUser;

@ContextConfiguration(locations={"classpath*:loxia-*.xml"})
public class GenericEntityDaoTests extends AbstractTestNGSpringContextTests {
	
	@Autowired
	private InnerUserDao userDao;
	
	@Test
	public void testUserDaoAdd(){
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
		userDao.executeDDL("shutdown");
	}
}
