package com.example.springforum.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springforum.model.Post;
import com.example.springforum.repository.PostDao;

@Transactional
@Service
public class PostService {

	@Autowired
	PostDao dao;

	// 全件取得用メソッド
	public List<Post> selectAll() {

		return dao.selectAll();
	}

	// 1件登録用メソッド
	public boolean insert(Post post) {

		//insert実行
		int rowCount = dao.insert(post);

		boolean result = false;
		if (rowCount > 0) { //insert成功
			result = true;
		}

		return result;
	}

	// 1件取得用メソッド
	public Post select(int postId) {

		return dao.select(postId);
	}

	// 1件更新用メソッド
	public boolean update(Post post) {

		//update実行
		int rowCount = dao.update(post);

		boolean result = false;
		if (rowCount > 0) { //insert成功
			result = true;
		}

		return result;
	}

	// 回答件数の更新用メソッド
	public boolean updateNumReplies(int postId) {

		// update実行
		int rowCount = dao.updateNumReplies(postId);

		boolean result = false;
		if (rowCount > 0) { //update成功
			result = true;
		}

		return result;
	}

	// 質問投稿の検索用メソッド
	public List<Post> search(String query) {

		// 検索フィールドの入力からキーワードリスト作成
		List<String> keywordList = new ArrayList<String>();

		// シングルクォートのエスケープ
		query = query.replace("'", "''");

		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(query);
		while(m.find()) {
			keywordList.add(m.group(1).replace("\"", ""));
		}

		// 検索実行
		List<Post> postList = dao.selectByKeywords(keywordList);

		// 検索結果に対する処理
		for(Post post : postList) {
			// エスケープ処理
			post.setPostTitle(post.getPostTitle().replaceAll("&", "&amp;"));
			post.setPostTitle(post.getPostTitle().replaceAll("<", "&lt;"));
			post.setPostTitle(post.getPostTitle().replaceAll(">", "&gt;"));
			post.setPostTitle(post.getPostTitle().replaceAll("\"", "&quot;"));
			post.setPostTitle(post.getPostTitle().replaceAll("'", "&#39;"));
			post.setPostMsg(post.getPostMsg().replaceAll("&", "&amp;"));
			post.setPostMsg(post.getPostMsg().replaceAll("<", "&lt;"));
			post.setPostMsg(post.getPostMsg().replaceAll(">", "&gt;"));
			post.setPostMsg(post.getPostMsg().replaceAll("\"", "&quot;"));
			post.setPostMsg(post.getPostMsg().replaceAll("'", "&#39;"));

			for(String keyword : keywordList) {
				// キーワード文字列の強調表示処理
				post.setPostTitle(post.getPostTitle().replaceAll("(?i)" + keyword, "<b><i>$0</i></b>"));
				post.setPostMsg(post.getPostMsg().replaceAll("(?i)" + keyword, "<b><i>$0</i></b>"));
			}
		}

		return postList;
	}

	// ユーザの質問投稿取得用メソッド
	public List<Post> selectByPostUserId(String userId) {

		return dao.selectByPostUserId(userId);
	}

	// ユーザが回答した質問投稿取得用メソッド
	public List<Post> selectByReplyUserId(String userId) {

		return dao.selectByReplyUserId(userId);
	}
}
