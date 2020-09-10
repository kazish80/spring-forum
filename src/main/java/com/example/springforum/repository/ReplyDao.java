package com.example.springforum.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.springforum.model.Reply;

@Repository
public class ReplyDao {

	@Autowired
	JdbcTemplate jdbc;

	//回答投稿を1件insert
	public int insert(Reply reply) throws DataAccessException {

		int rowCount = jdbc.update("INSERT INTO m_reply("
				+ " reply_msg,"
				+ " reply_user_id,"
				+ " reply_user_name,"
				+ " post_id,"
				+ " created_at,"
				+ " updated_at)"
				+ " VALUES(?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
				reply.getReplyMsg(),
				reply.getReplyUserId(),
				reply.getReplyUserName(),
				reply.getPostId());

		return rowCount;
	}

	// 質問に紐づく回答を全件取得
	public List<Reply> selectByPostId(int postId) throws DataAccessException {

		String sql = "SELECT * FROM m_reply WHERE post_id = ?";

		//RowMapperの生成
		RowMapper<Reply> rowMapper = new BeanPropertyRowMapper<Reply>(Reply.class);

		// SQL実行
		return jdbc.query(sql, rowMapper, postId);
	}

	// 回答投稿を1件取得
	public Reply select(int replyId) throws DataAccessException {

		String sql = "SELECT * FROM m_reply WHERE reply_id = ?";

		//RowMapperの生成
		RowMapper<Reply> rowMapper = new BeanPropertyRowMapper<Reply>(Reply.class);

		//SQL実行
		return jdbc.queryForObject(sql, rowMapper, replyId);
	}

	//回答投稿を1件update
	public int update(Reply reply) throws DataAccessException {

		int rowCount = jdbc.update("UPDATE m_reply"
					+ " SET reply_msg = ?"
					+ " WHERE reply_id = ?",
					reply.getReplyMsg(),
					reply.getReplyId());

		return rowCount;
	}
}
