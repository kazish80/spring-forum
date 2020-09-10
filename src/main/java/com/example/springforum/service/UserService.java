package com.example.springforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springforum.model.User;
import com.example.springforum.repository.UserDao;

@Transactional
@Service
public class UserService {

	@Autowired
	UserDao dao;

	// 1件取得メソッド
	public User select(String userId) {

		return dao.select(userId);
	}

	// 1件登録メソッド
	public boolean insert(User user) {

		//insert実行
		int rowCount = dao.insert(user);

		boolean result = false;
		if (rowCount > 0) { //insert成功
			result = true;
		}

		return result;
	}

	// 1件更新メソッド
	public boolean update(User user) {

		//update実行
		int rowCount = dao.update(user);

		boolean result = false;
		if (rowCount > 0) { //update成功
			result = true;
		}

		return result;
	}
}
