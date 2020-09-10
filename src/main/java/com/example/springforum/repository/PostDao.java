package com.example.springforum.repository;

import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.springforum.model.Post;

@Repository
public class PostDao {

	@Autowired
	JdbcTemplate jdbc;

	// 質問投稿を全件取得
	public List<Post> selectAll() {

		//全件取得するSQL
		String sql = "SELECT * FROM m_post ORDER BY post_id DESC";

		//RowMapperの生成
		RowMapper<Post> rowMapper = new BeanPropertyRowMapper<Post>(Post.class);

		// SQL実行
		return jdbc.query(sql, rowMapper);
	}

	//質問投稿を1件insert
	public int insert(Post post) throws DataAccessException {

		int rowCount = jdbc.update("INSERT INTO m_post("
				+ " post_title,"
				+ " post_msg,"
				+ " post_user_id,"
				+ " post_user_name,"
				+ " num_replies,"
				+ " created_at,"
				+ " updated_at)"
				+ " VALUES(?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
				post.getPostTitle(),
				post.getPostMsg(),
				post.getPostUserId(),
				post.getPostUserName(),
				0);

		return rowCount;
	}

	// 質問投稿を1件取得
	public Post select(int postId) throws DataAccessException {

		String sql = "SELECT * FROM m_post WHERE post_id = ?";

		//RowMapperの生成
		RowMapper<Post> rowMapper = new BeanPropertyRowMapper<Post>(Post.class);

		//SQL実行
		return jdbc.queryForObject(sql, rowMapper, postId);
	}

	//質問投稿を1件update
	public int update(Post post) throws DataAccessException {

		int rowCount = jdbc.update("UPDATE m_post"
						+ " SET post_title = ?, post_msg = ?"
						+ " WHERE post_id = ?",
						post.getPostTitle(),
						post.getPostMsg(),
						post.getPostId());

		return rowCount;
	}

	// 回答件数の更新
	public int updateNumReplies(int postId) throws DataAccessException {

		int rowCount = jdbc.update("UPDATE m_post"
						+ " SET num_replies = num_replies + 1"
						+ " WHERE post_id = ?", postId);

		return rowCount;
	}

	// キーワード検索
	public List<Post> selectByKeywords(List<String> keywordList) {

		ListIterator<String> it = keywordList.listIterator();

		String str = "";
		if(it.hasNext()) {
			str = str.concat("WHERE CONCAT(post_title, ' ', post_msg) LIKE '%" + (String)it.next() + "%'");
		}

		while(it.hasNext()) {
			str = str.concat(" AND CONCAT(post_title, ' ', post_msg) LIKE '%" + (String)it.next() + "%'");
		}

		// SQL作成
		String sql = "SELECT * FROM m_post " + str + " ORDER BY post_id DESC";

		System.out.println("sql = [" + sql + "]");

		//RowMapperの生成
		RowMapper<Post> rowMapper = new BeanPropertyRowMapper<Post>(Post.class);

		// SQL実行
		return jdbc.query(sql, rowMapper);
	}

	// 当該ユーザーの質問投稿を全件取得
	public List<Post> selectByPostUserId(String userId) {

		// SQL作成
		String sql = "SELECT * FROM m_post WHERE post_user_id = ? ORDER BY post_id DESC";

		//RowMapperの生成
		RowMapper<Post> rowMapper = new BeanPropertyRowMapper<Post>(Post.class);

		// SQL実行
		return jdbc.query(sql, rowMapper, userId);
	}

	// 当該ユーザーが回答した質問投稿を全件取得
	public List<Post> selectByReplyUserId(String userId) {

		// SQL作成
		String sql = "SELECT * FROM m_post" +
					" WHERE post_id = ANY (SELECT post_id FROM m_reply WHERE reply_user_id = ?)" +
					" AND post_user_id != ?" +
					" ORDER BY post_id DESC";

		//RowMapperの生成
		RowMapper<Post> rowMapper = new BeanPropertyRowMapper<Post>(Post.class);

		// SQL実行
		return jdbc.query(sql, rowMapper, userId, userId);
	}
}
