package com.example.springforum.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Component
public class GlobalControllAdvice {

	/*
	@ExceptionHandler(DataAccessException.class)
	public String dataAccessExceptionHandler(DataAccessException e, Model model) {

		//例外クラスのerrorをModelに登録
		model.addAttribute("error", "内部サーバーエラー");

		//例外クラスのmessageをModelに登録
		model.addAttribute("message", "DB処理で Exceptionが発生しました");

		//HTTPエラーコード(500) をModelに登録
		model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

		return "error";
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(Exception e, Model model) {

		//例外クラスのerrorをModelに登録
		model.addAttribute("error", "内部サーバーエラー");

		//例外クラスのmessageをModelに登録
		model.addAttribute("message", "Exceptionが発生しました");

		//HTTPエラーコード(500) をModelに登録
		model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

		return "error";
	}
	*/
}
