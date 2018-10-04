package springbook.user.service;

import static springbook.user.service.UserService.MIN_LOGOUT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;

//import static springbook.user.service.LevelUpgradeEvent.MIN_LOGOUT_FOR_SILVER;
//import static springbook.user.service.LevelUpgradeEvent.MIN_RECOMMEND_FOR_GOLD;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	List<User> users;
	User user;
	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;
	@Autowired DataSource dataSource;
	@Autowired MailSender mailSender;
	
	@Before
	public void setUp() {
		user = new User();
		
		users = Arrays.asList(
				new User("bumjin", "�ڹ���", "p1", "test1@test.com", Level.BASIC, MIN_LOGOUT_FOR_SILVER-1, 0),
				new User("joytouch", "����", "p2", "test2@test.com", Level.BASIC, MIN_LOGOUT_FOR_SILVER, 0),
				new User("erwins", "�Ž���", "p3", "test3@test.com", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1),
				new User("madnite1", "�̻�ȣ", "p4", "test4@test.com", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
				new User("green", "���α�", "p5", "test5@test.com", Level.GOLD, 100, Integer.MAX_VALUE));
				
	}
	
	@Test
	public void upgradeLevels() {
		userDao.deleteAll();
		for(User user: users) userDao.add(user);
		
		//userService.upgradeLevels();
		
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
		
	}
	
	
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	
	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if(level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		for(Level level: levels) {
			if (level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
	
	@Autowired PlatformTransactionManager transactionManager;
	@Test
	public void upgradeAllorNothing() {
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setTransactionManager(transactionManager);
		userDao.deleteAll();
		for(User user: users) userDao.add(user);
		
		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		}
		catch(TestUserServiceException e) {}
		
		checkLevelUpgraded(users.get(1), false);
		testUserService.setMailSender(mailSender);
	}
	
	static class TestUserService extends UserService {
		private String id;
		
		private TestUserService(String id) {
			this.id = id;
		}
		
		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) 
				throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	static class TestUserServiceException extends RuntimeException{}
	
	private void checkLevelUpgraded(User user, Boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if(upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		}
		else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
}
