//client

package springbook.user.dao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import org.junit.runner.*;


import java.sql.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;

import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDaoTest {
	@Autowired
	private UserDao dao;
	
	private User user0;
	private User user1;
	private User user2;
	private User user3;
		
	@Before
	public void setUp() {
		this.user0 = new User("test1", "º€¡÷«Â", "P@ssw0rd", Level.BASIC, 1, 0, "test1@test.com");
		this.user1 = new User("test2", "¿Ã¡¯»£", "P@ssw0rd", Level.SILVER, 55, 10, "test2@test.com");
		this.user2 = new User("test3", "ø¡Ω¬√∂", "P@ssw0rd", Level.GOLD, 100, 40, "test3@test.com");
		this.user3 = new User("test4", "¿ÃªÛπŒ", "P@ssw0rd", Level.SILVER, 22, 21, "test4@test.com");
	}
	
	@Test
	public void addAndGet() throws SQLException{
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user0); dao.add(user1); dao.add(user2); dao.add(user3);
		
		User userget0 = dao.get(user0.getId());
		checkSameUser(userget0, user0);
		
		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
		
		User userget3 = dao.get(user3.getId());
		checkSameUser(userget3, user3);
	}
	
	@Test
	public void getCount() throws SQLException{
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user0);
		assertThat(dao.getCount(), is(1));
		dao.add(user1);
		assertThat(dao.getCount(), is(2));
		dao.add(user2);
		assertThat(dao.getCount(), is(3));
		dao.add(user3);
		assertThat(dao.getCount(), is(4));
	}
	

	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailuer() throws SQLException{
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
		
	}
	
	@Test(expected=DataAccessException.class)
	public void duplicateKey() {
		dao.deleteAll();
		
		dao.add(user0);
		dao.add(user0);
	}
	
	@Test
	public void getAll() throws SQLException {
		dao.deleteAll();
		
		List<User> users4 = dao.getAll();
		assertThat(users4.size(), is(0));
		
		dao.add(user0);
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(1));
		checkSameUser(user0, users0.get(0));
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(2));
		checkSameUser(user0, users0.get(0));
		checkSameUser(user1, users1.get(1));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(3));
		checkSameUser(user0, users0.get(0));
		checkSameUser(user1, users1.get(1));
		checkSameUser(user2, users2.get(2));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(4));
		checkSameUser(user0, users0.get(0));
		checkSameUser(user1, users1.get(1));
		checkSameUser(user2, users2.get(2));
		checkSameUser(user3, users3.get(3));
	}
	
	@Test
	public void update() {
		dao.deleteAll();
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("ø¿πŒ±‘");
		user1.setPassword("P@ssword");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		user1.setEmail("test5@test.com");
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.user.dao.UserDaoTest");
	} 
}
