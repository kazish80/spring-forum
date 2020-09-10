package com.example.springforum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springforum.model.Reply;
import com.example.springforum.repository.ReplyDao;

@Transactional
@Service
public class ReplyService {

	@Autowired
	ReplyDao dao;

	// 1件登録用メソッド
	public boolean insert(Reply reply) {

		//insert実行
		int rowCount = dao.insert(reply);

		boolean result = false;
		if (rowCount > 0) { //insert成功
			result = true;
		}

		return result;
	}

	// 質問投稿に対する回答を全件取得するメソッド
	public List<Reply> selectByPostId(int postId) {

		return dao.selectByPostId(postId);
	}

	// 1件更新用メソッド
	public boolean update(Reply reply) {

		//update実行
		int rowCount = dao.update(reply);

		boolean result = false;
		if (rowCount > 0) { //insert成功
			result = true;
		}

		return result;
	}

	public Reply select(int replyId) {

		return dao.select(replyId);
	}
}
