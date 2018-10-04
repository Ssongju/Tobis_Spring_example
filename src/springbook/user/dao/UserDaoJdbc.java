package springbook.user.dao;

import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.List;

import springbook.user.domain.*;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbctemplate;


	public void setDataSource(DataSource dataSource) {
		this.jdbctemplate = new JdbcTemplate(dataSource);
	}

	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));

			return user;
		}
	};

	public void add(final User user) {
		this.jdbctemplate.update(
				"insert into users(id, name, password, level, login, recommend, email)"
				+ " values(?, ?, ?, ?, ?, ?, ?)",
				user.getId(), user.getName(), user.getPassword(),
				user.getLevel().intValue(), user.getLogin(),
				user.getRecommend(), user.getEmail());
	}

	public void deleteAll() {
		
		this.jdbctemplate.update("delete from users");
	}

	public User get(String id) {
		return this.jdbctemplate.queryForObject("select * from users where id = ?", new Object[] { id },
				this.userMapper);
	}

	public List<User> getAll() {
		return this.jdbctemplate.query("select * from users order by id", this.userMapper);
	}

	public int getCount() {
		return this.jdbctemplate.queryForObject("select count(*) from users", Integer.class);
	}


	public void update(User user) {
		this.jdbctemplate.update(
				"update users set name = ?, password = ?, level = ?,"
				+ "login = ?, recommend = ?, email = ?"
				+ "where id = ?", user.getName(), user.getPassword(),
				user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
				user.getId(), user.getEmail());
		
	}

	
}