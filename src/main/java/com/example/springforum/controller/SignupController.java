package com.example.springforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.springforum.model.GroupOrder;
import com.example.springforum.model.SignupForm;
import com.example.springforum.model.User;
import com.example.springforum.service.UserService;

@Controller
public class SignupController {

	@Autowired
	private UserService userService;

	@GetMapping("/signup")
	public String getSignup(@ModelAttribute SignupForm form, Model model) {

		model.addAttribute("contents", "signup :: signup_contents");

		return "layout";
	}

	@PostMapping("/signup")
	public String postSignup(@ModelAttribute @Validated(GroupOrder.class) SignupForm form,
									BindingResult bindingResult,
													Model model) {

		System.out.println("バインドエラー = " + bindingResult.hasErrors());

		// データバインド失敗の場合
		// 入力チェックに引っかかった場合、ユーザー登録画面に戻る
		if (bindingResult.hasErrors()) {
			// GETリクエスト用のメソッドを呼び出して、ユーザー登録画面に戻る
			return getSignup(form, model);
		}

		User user = new User();

		user.setUserId(form.getUserId());
		user.setPassword(form.getPassword());
		user.setUserName(form.getUserName());
		user.setRole("ROLE_GENERAL");

		//ユーザー登録処理
		try {
			boolean result = userService.insert(user);

			//ユーザー登録結果の判定
			if(result == true) {
				System.out.println("insert成功");
			} else {
				System.out.println("insert失敗");
			}
		} catch(DuplicateKeyException e) {
			model.addAttribute("errorMsg", "メールアドレスは既に登録されています。");
			return getSignup(form, model);
		}

		// login.htmlにリダイレクト
		return "redirect:/login";
	}

}
