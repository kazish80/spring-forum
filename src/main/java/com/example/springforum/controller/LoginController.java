package com.example.springforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	// ログイン画面のGET用コントローラー
	@GetMapping("/login")
	public String getLogin(@RequestParam(name="error", required=false) Integer error, Model model) {

		if(error != null) {
			model.addAttribute("error", 1);
		}

		model.addAttribute("contents", "login :: login_contents");

		return "layout";
	}

	// ログイン画面のPOST用コントローラー
	@PostMapping("/login")
	public String postLogin(Model model) {

		// home.html に画面遷移
		return "redirect:/";
	}

}
