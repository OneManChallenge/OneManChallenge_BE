package com.hanghae.onemanitnews.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.onemanitnews.common.response.SuccessResponse;
import com.hanghae.onemanitnews.controller.request.LoginMemberRequest;
import com.hanghae.onemanitnews.controller.request.SaveMemberRequest;
import com.hanghae.onemanitnews.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberApiController {

	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody @Valid SaveMemberRequest saveMemberRequest) {

		memberService.signup(saveMemberRequest);

		return new ResponseEntity<>(SuccessResponse.builder()
			.msg("회원가입 성공하였습니다.")
			.build(),
			HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid LoginMemberRequest loginMemberRequest,
		HttpServletResponse response) {

		memberService.login(loginMemberRequest, response);

		return new ResponseEntity<>(SuccessResponse.builder()
			.msg("로그인이 완료되었습니다.")
			.build(),
			HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> login() {

		return new ResponseEntity<>(SuccessResponse.builder()
			.msg("로그아웃 성공하였습니다.")
			.build(),
			HttpStatus.OK);
	}
}
