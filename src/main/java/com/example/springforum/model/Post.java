package com.example.springforum.model;

import java.util.Date;

import lombok.Data;

@Data
public class Post {

	private Integer postId;
	private String postTitle;
	private String postMsg;
	private Date createdAt;
	private Date updatedAt;
	private String postUserId;
	private String postUserName;
	private Integer numReplies;
}
