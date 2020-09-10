package com.example.springforum.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.springforum.model.User;

@Repository
public class UserDao {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	PasswordEncoder passwordEncoder;

	public User select(String userId) throws DataAccessException {

		//1件取得用SQL
		String sql = "SELECT * FROM m_user WHERE user_id = ?";

		//RowMapperの生成
		RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);

		//SQL実行
		return jdbc.queryForObject(sql, rowMapper, userId);
	}

	public int insert(User user) throws DuplicateKeyException, DataAccessException {

		// パスワード暗号化
		String password = passwordEncoder.encode(user.getPassword());

		int rowCount = jdbc.update("INSERT INTO m_user("
					+ " user_id,"
					+ " password,"
					+ " user_name,"
					+ " role)"
					+ " VALUES(?, ?, ?, ?)",
					user.getUserId(),
					password,
					user.getUserName(),
					user.getRole());

		return rowCount;
	}

	public int update(User user) throws DataAccessException {

		int rowCount;
		if (user.getPassword().isEmpty()) {
			rowCount = jdbc.update("UPDATE m_user"
								+ " SET user_name = ?"
								+ " WHERE user_id = ?",
								user.getUserName(),
								user.getUserId());
		} else {
			String password = passwordEncoder.encode(user.getPassword());
			rowCount = jdbc.update("UPDATE m_user"
								+ " SET user_name = ?, password = ?"
								+ " WHERE user_id = ?",
								user.getUserName(),
								password,
								user.getUserId());
		}

		return rowCount;
	}
}
