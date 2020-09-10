package com.example.springforum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springforum.model.AppUserDetails;
import com.example.springforum.model.Post;
import com.example.springforum.model.PostForm;
import com.example.springforum.model.Reply;
import com.example.springforum.service.PostService;
import com.example.springforum.service.ReplyService;
import com.example.springforum.service.UserService;

@Controller
public class PostController {

	@Autowired
	private PostService postService;
	@Autowired
	private UserService userService;
	@Autowired
	private ReplyService replyService;

	@GetMapping("/post/new")
	public String getPostCreate(@ModelAttribute PostForm form, Model model) {

		// postNew.htmlに画面遷移
		model.addAttribute("contents", "postNew :: postNew_contents");

		return "layout";
	}

	@PostMapping("/post/new")
	public String postPostCreate(@RequestParam("action") String action,
							@ModelAttribute @Validated PostForm form,
							BindingResult bindingResult,
							Model model,
							//@AuthenticationPrincipal User loginUser) {
							@AuthenticationPrincipal AppUserDetails loginUser) {

		if(action.equals("send")) {

			// データバインド失敗の場合
			// 入力チェックに引っかかった場合、質問投稿画面に戻る
			if (bindingResult.hasErrors()) {
				// GETリクエスト用のメソッドを呼び出して、質問投稿画面に戻る
				return getPostCreate(form, model);
			}

			String postUserId = loginUser.getUsername();
			String postUserName = userService.select(postUserId).getUserName();

			Post post = new Post();
			post.setPostTitle(form.getPostTitle());
			post.setPostMsg(form.getPostMsg());
			post.setPostUserId(postUserId);
			post.setPostUserName(postUserName);

			//質問投稿の登録処理
			boolean result = postService.insert(post);

			//質問投稿の登録結果の判定
			if(result == true) {
				System.out.println("insert成功");
			} else {
				System.out.println("insert失敗");
			}

		}

		return "redirect:/";
	}

	@GetMapping("/post/{id}")
	public String getPostView(@PathVariable("id") int postId, Model model) {

		// DBから質問投稿を取得
		Post post = postService.select(postId);

		List<Reply> replyList = null;

		// 回答データがある場合
		if(post.getNumReplies() > 0) {

			// DBから回答データを取得
			replyList = replyService.selectByPostId(postId);
		}

		model.addAttribute("post", post);
		model.addAttribute("replyList", replyList);
		model.addAttribute("replyClicked", false);

		// postDetail.htmlに画面遷移
		model.addAttribute("contents", "postDetail :: postDetail_contents");

		return "layout";
	}

	@GetMapping("/post/{id}/reply")
	public String getPostReply(@PathVariable("id") int postId, Model model) {

		// DBから質問投稿を取得
		Post post = postService.select(postId);

		List<Reply> replyList = null;

		// 回答データがある場合
		if(post.getNumReplies() > 0) {

			// DBから回答データを取得
			replyList = replyService.selectByPostId(postId);
		}

		model.addAttribute("post", post);
		model.addAttribute("replyList", replyList);
		model.addAttribute("replyClicked", true);

		// postDetail.htmlを再表示
		model.addAttribute("contents", "postDetail :: postDetail_contents");

		return "layout";
	}

	@PostMapping("/post/{id}/reply")
	public String postPostReply(@PathVariable("id") int postId,
								@RequestParam("action") String action,
								@RequestParam("replyMsg") String replyMsg,
								Model model,
								//@AuthenticationPrincipal User loginUser) {
								@AuthenticationPrincipal AppUserDetails loginUser) {

		if(action.equals("send")) {

			// 回答がblankの場合
			if(replyMsg.replaceAll("　",  "").replaceAll(" ", "").length() == 0) {

				model.addAttribute("replyMsgError", true);

				// GETリクエスト用のメソッドを呼び出して、回答投稿画面に戻る
				return getPostReply(postId, model);
			}

			//回答投稿の登録処理
			String replyUserId = loginUser.getUsername();
			String replyUserName = userService.select(replyUserId).getUserName();

			Reply reply = new Reply();

			reply.setReplyMsg(replyMsg);
			reply.setReplyUserId(replyUserId);
			reply.setReplyUserName(replyUserName);
			reply.setPostId(postId);

			boolean result = replyService.insert(reply);

			if(result == true) {

				System.out.println("回答insert成功");

				// 質問投稿の回答件数の更新
				result = postService.updateNumReplies(postId);
				if(result == true) {
					System.out.println("質問投稿テーブルの回答件数のupdate成功");
				} else {
					System.out.println("質問投稿テーブルの回答件数のupdate失敗");
				}

			} else {
				System.out.println("回答insert失敗");
			}
		}

		return "redirect:/post/" + postId;
	}

	@GetMapping("/post/{id}/edit")
	public String getPostEdit(@PathVariable("id") int postId,
								Model model,
								@AuthenticationPrincipal AppUserDetails loginUser) {

		// DBから質問投稿を取得
		Post post = postService.select(postId);

		// ログインユーザが質問投稿者ではない場合、編集権限なしのため質問編集画面に遷移しない
		if( !post.getPostUserId().equals(loginUser.getUserId()) ) {
			return "redirect:/post/" + postId;
		}

		model.addAttribute("postForm", post);

		// 編集画面 (postEdit.html) に遷移
		model.addAttribute("contents", "postEdit :: postEdit_contents");

		return "layout";
	}

	@PostMapping("/post/{id}/edit")
	public String postPostEdit(@RequestParam("action") String action,
								@ModelAttribute @Validated PostForm form,
								BindingResult bindingResult,
								@PathVariable("id") int postId,
								Model model,
								@AuthenticationPrincipal AppUserDetails loginUser) {

		if(action.equals("send")) {

			// データバインド失敗の場合
			// 入力チェックに引っかかった場合、質問編集画面に戻る
			if (bindingResult.hasErrors()) {

				model.addAttribute("postForm", form);

				// 質問編集画面 (postEdit.html) に戻る
				model.addAttribute("contents", "postEdit :: postEdit_contents");

				return "layout";
			}

			Post post = postService.select(postId);
			post.setPostTitle(form.getPostTitle());
			post.setPostMsg(form.getPostMsg());

			//質問投稿の更新処理
			boolean result = postService.update(post);

			//更新結果の判定
			if(result == true) {
				System.out.println("update成功");
			} else {
				System.out.println("update失敗");
			}
		}

		return "redirect:/post/" + postId;
	}

	@GetMapping("/post/{pid}/reply/{rid}/edit")
	public String getReplyEdit(@PathVariable("pid") int postId,
								@PathVariable("rid") int replyId,
								Model model,
								@AuthenticationPrincipal AppUserDetails loginUser) {

		// DBから回答投稿を取得
		Reply reply = replyService.select(replyId);

		// ログインユーザが回答投稿者ではない場合、編集権限なしのため回答編集画面に遷移しない
		if( !reply.getReplyUserId().equals(loginUser.getUserId()) ) {
			return "redirect:/post/" + postId;
		}

		model.addAttribute("postId", postId);
		model.addAttribute("replyId", replyId);
		model.addAttribute("replyMsg", reply.getReplyMsg());

		// postEdit.htmlに画面遷移
		model.addAttribute("contents", "replyEdit :: replyEdit_contents");

		return "layout";
	}

	@PostMapping("/post/{pid}/reply/{rid}/edit")
	public String postReplyEdit(@PathVariable("pid") int postId,
								@PathVariable("rid") int replyId,
								@RequestParam("action") String action,
								@RequestParam("replyMsg") String replyMsg,
								Model model) {

		if(action.equals("send")) {

			// 回答がblankの場合
			if(replyMsg.replaceAll("　",  "").replaceAll(" ", "").length() == 0) {

				model.addAttribute("replyMsgError", true);
				model.addAttribute("postId", postId);
				model.addAttribute("replyId", replyId);
				model.addAttribute("replyMsg", replyMsg);

				// 回答編集画面 (replyEdit.html) に戻る
				model.addAttribute("contents", "replyEdit :: replyEdit_contents");

				return "layout";
			}

			//回答投稿の更新処理
			Reply reply = replyService.select(replyId);
			reply.setReplyMsg(replyMsg);

			//DB更新
			boolean result = replyService.update(reply);
			if(result == true) {
				System.out.println("回答update成功");
			} else {
				System.out.println("回答update失敗");
			}
		}

		return "redirect:/post/" + postId;
	}

}
