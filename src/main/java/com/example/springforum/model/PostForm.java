package com.example.springforum.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PostForm {
	private Integer postId;

	@NotBlank(message = "{require_check}")
	private String postTitle;

	@NotBlank(message = "{require_check}")
	private String postMsg;

}
