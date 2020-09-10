package com.example.springforum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springforum.model.AppUserDetails;
import com.example.springforum.model.GroupOrder;
import com.example.springforum.model.Post;
import com.example.springforum.model.User;
import com.example.springforum.model.UserEditForm;
import com.example.springforum.service.PostService;
import com.example.springforum.service.UserService;

@Controller
public class UserController {

	private final int MAX_PAGE_SIZE = 10;

	@Autowired
	private PostService postService;
	@Autowired
	private UserService userService;

	@GetMapping("/user/info")
	public String getUserInfo(
				@RequestParam(value = "page", required = false, defaultValue = "0") int page,
				Model model,
				@AuthenticationPrincipal AppUserDetails loginUser) {

		// ユーザーの質問投稿の一覧を生成
		List<Post> postList = postService.selectByPostUserId(loginUser.getUserId());

		Pageable pageable = PageRequest.of(page, MAX_PAGE_SIZE, Sort.unsorted());

		int start = (int)pageable.getOffset();
		int end = (start + pageable.getPageSize()) > postList.size() ? postList.size() : (start + pageable.getPageSize());
		Page<Post> postPage = new PageImpl<Post>(postList.subList(start, end), pageable, postList.size());

		//Modelに質問投稿リストを登録
		model.addAttribute("page", postPage);
		model.addAttribute("postList", postPage.getContent());  // 投稿データ(List<Post>)を取り出してModelにセット
		model.addAttribute("url", "/user/info");

		model.addAttribute("contents", "userInfo :: userInfo_contents");

		return "layout";
	}

	@GetMapping("/user/info2")
	public String getUserInfo2(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			Model model,
			@AuthenticationPrincipal AppUserDetails loginUser) {

		// ユーザーが回答した質問投稿の一覧を生成
		List<Post> postList = postService.selectByReplyUserId(loginUser.getUserId());

		Pageable pageable = PageRequest.of(page, MAX_PAGE_SIZE, Sort.unsorted());

		int start = (int)pageable.getOffset();
		int end = (start + pageable.getPageSize()) > postList.size() ? postList.size() : (start + pageable.getPageSize());
		Page<Post> postPage = new PageImpl<Post>(postList.subList(start, end), pageable, postList.size());

		//Modelに質問投稿リストを登録
		model.addAttribute("page", postPage);
		model.addAttribute("postList", postPage.getContent());  // 投稿データ(List<Post>)を取り出してModelにセット
		model.addAttribute("url", "/user/info2");

		model.addAttribute("contents", "userInfo2 :: userInfo2_contents");

		return "layout";
	}

	@GetMapping("/user/edit")
	public String getUserEdit(@ModelAttribute UserEditForm form,
							Model model,
							@AuthenticationPrincipal AppUserDetails loginUser) {

		form.setUserId(loginUser.getUserId());
		form.setUserName(loginUser.getAppUserName());
		form.setPassword(loginUser.getPassword());

		model.addAttribute("contents", "userEdit :: userEdit_contents");

		return "layout";
	}

	@PostMapping("/user/edit")
	public String postUserEdit(@ModelAttribute @Validated(GroupOrder.class) UserEditForm form,
								BindingResult bindingResult,
								Model model,
								@AuthenticationPrincipal AppUserDetails loginUser) {

		System.out.println("ユーザーID : " + form.getUserId());
		System.out.println("ユーザー名 : " + form.getUserName());
		System.out.println("パスワード : " + form.getPassword());

		// データバインド失敗の場合
		// 入力チェックに引っかかった場合、ユーザー編集画面に戻る
		if (bindingResult.hasErrors()) {
			model.addAttribute("userEditForm", form);

			// ユーザー編集画面 (postEdit.html) に戻る
			model.addAttribute("contents", "userEdit :: userEdit_contents");

			return "layout";
		}

		if(form.getUserId().equals(loginUser.getUserId())) {

			User user = new User();
			user.setUserId(form.getUserId());
			user.setUserName(form.getUserName());
			user.setPassword(form.getPassword());

			//質問投稿の更新処理
			boolean result = userService.update(user);

			//更新結果の判定
			if(result == true) {
				System.out.println("update成功");

				user = userService.select(form.getUserId());
				loginUser.setAppUserName(user.getUserName());
				loginUser.setPassword(user.getPassword());

			} else {
				System.out.println("update失敗");
			}
		}

		// userInfo.htmlにリダイレクト
		return "redirect:/user/info";
	}
}
