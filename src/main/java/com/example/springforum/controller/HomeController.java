package com.example.springforum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springforum.model.Post;
import com.example.springforum.service.PostService;
import com.example.springforum.service.UserService;

@Controller
public class HomeController {

	private final int MAX_PAGE_SIZE = 12;

	@Autowired
	PostService postService;
	@Autowired
	UserService userService;

	@GetMapping("/")
	public String getHome(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			Model model) {

		//質問投稿一覧の生成
		List<Post> postList = postService.selectAll();

		Pageable pageable = PageRequest.of(page, MAX_PAGE_SIZE, Sort.unsorted());

		int start = (int)pageable.getOffset();
		int end = (start + pageable.getPageSize()) > postList.size() ? postList.size() : (start + pageable.getPageSize());
		Page<Post> postPage = new PageImpl<Post>(postList.subList(start, end), pageable, postList.size());

		//Modelに質問投稿リストを登録
		model.addAttribute("page", postPage);
		model.addAttribute("postList", postPage.getContent());  // List<Post>に変換される
		model.addAttribute("url", "/");

		model.addAttribute("contents", "home :: home_contents");

		return "layout";
	}

	@GetMapping("/appinfo")
	public String getAppInfo(Model model) {

		model.addAttribute("contents", "appinfo :: appinfo_contents");

		return "layout";
	}
}
