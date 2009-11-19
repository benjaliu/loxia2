package cn.benjamin.loxia.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import cn.benjamin.loxia.model.User;

@ContextConfiguration(locations={"classpath*:loxia-*.xml"})
public class GenericEntityDaoTests extends AbstractTestNGSpringContextTests {
	
	@Autowired
	private UserDao userDao;
	
	@Test
	public void testUserDaoAdd(){
		User user = new User();
		user.setId(1l);
		user.setLoginName("user");
		user.setPassword("loxia");
		user.setUserName("Loxia User");
		userDao.save(user);
		System.out.println("==============================");
		User u = userDao.getByPrimaryKey(1l);
		assert u != null : "user is null";
		assert u.getLoginName().equals("user") : "wrong user";
		System.out.println("==============================");
		u.setUserName("Dragon");
		userDao.save(u);
		System.out.println("==============================");
		u = userDao.getByPrimaryKey(1l);
		assert u.getUserName().equals("Dragon") : "wrong user name";
		System.out.println("==============================");
		user = new User();
		user.setId(1l);
		user.setLoginName("user");
		user.setPassword("loxia");
		user.setUserName("Loxia User");
		userDao.save(user);
		System.out.println("==============================");
		List<User> users = userDao.findUsers(new Sort[]{new Sort("u.userName")});		
		u = userDao.findUserByLoginName("user");
		System.out.println(users.size());
		System.out.println(u.getUserName());
		List<User> anoUsers = userDao.findUserByName(0, 20, "U", new Sort[]{new Sort("u.userName")});
		System.out.println(anoUsers.size());
		System.out.println(userDao.findByLoginName(0, 20, null).size());
		System.out.println(userDao.findByLoginName(0, 20, "a").size());
		userDao.findByLoginNameSql(0, 20, "u", null, null);
		userDao.findByLoginNameSql(0, 20, "u", null, "w");
	}
}
