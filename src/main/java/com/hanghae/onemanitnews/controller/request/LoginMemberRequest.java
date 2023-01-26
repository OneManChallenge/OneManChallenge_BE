package com.hanghae.onemanitnews.controller.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Getter;

@Getter
public class LoginMemberRequest {

	@NotBlank(message = "공백 또는 빈값을 허용하지 않습니다.")
	@Email(message = "이메일(@) 형식이 아닙니다.")
	private String email;

	@NotBlank(message = "공백 또는 빈값을 허용하지 않습니다.")
	@Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8 ~ 16자이내, 영대소문자/숫자/특수문자 3종류 조합")
	private String password;
}
