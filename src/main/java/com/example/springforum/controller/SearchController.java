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

@Controller
public class SearchController {

	private final int MAX_PAGE_SIZE = 10;

	@Autowired
	PostService postService;

	@GetMapping("/search")
	public String getSearch(@RequestParam("query") String query,
							@RequestParam(value = "page", required = false, defaultValue = "0") int page,
							Model model) {

		//System.out.println("query : [" + query + "]");

		// 検索フィールドの入力がない、またはBlankの場合
		if(query.replaceAll("　",  "").replaceAll(" ", "").length() == 0) {

			model.addAttribute("query", query);
			model.addAttribute("numPosts", 0);

		} else {

			List<Post> postList = postService.search(query);

			Pageable pageable = PageRequest.of(page, MAX_PAGE_SIZE, Sort.unsorted());

			int start = (int)pageable.getOffset();
			int end = (start + pageable.getPageSize()) > postList.size() ? postList.size() : (start + pageable.getPageSize());
			Page<Post> postPage = new PageImpl<Post>(postList.subList(start, end), pageable, postList.size());

			model.addAttribute("page", postPage);
			model.addAttribute("postList", postPage.getContent());  // 1ページ分の投稿情報を postListの名前で渡す
			model.addAttribute("numPosts", postList.size());		// ヒット件数 (postListはヒットした全ての投稿情報を持つ)
			model.addAttribute("query", query);
			model.addAttribute("url", "/search");
		}

		model.addAttribute("contents", "search :: search_contents");

		return "layout";
	}
}
