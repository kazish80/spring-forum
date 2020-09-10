package com.example.springforum.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class SignupForm {

	// 必須入力
	@NotBlank(groups=ValidGroup1.class, message = "{require_check}")
	private String userName; // ユーザー名

	// 必須入力、メールアドレス形式
	@NotBlank(groups=ValidGroup1.class, message = "{require_check}")
	@Email(groups=ValidGroup2.class, message = "{email_check}")
	private String userId; // ユーザーID

	// 必須入力、長さ4から100桁まで、半角英数字のみ
	@NotBlank(groups=ValidGroup1.class, message = "{require_check}")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", groups=ValidGroup2.class, message = "{pattern_check}")
	@Size(min = 4, max = 100, groups=ValidGroup3.class, message = "{size_check}")
	private String password; // パスワード
}
