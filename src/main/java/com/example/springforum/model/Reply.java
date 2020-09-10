package com.example.springforum.model;

import java.util.Date;

import lombok.Data;

@Data
public class Reply {

	private Integer replyId;
	private String replyMsg;
	private Date createdAt;
	private Date updatedAt;
	private String replyUserId;
	private String replyUserName;
	private Integer postId;
}
