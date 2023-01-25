package com.hanghae.onemanitnews.common.exception;

import lombok.Getter;

@Getter
public enum CommonExceptionEnum {
	/* News 예외 메시지 */
	NEWS_NOT_FOUND_MSG("뉴스가 검색되지 않습니다."),

	/* Member 예외 메시지 */
	DUPLICATE_EMAIL("이미 가입한 Email입니다."),
	MEMBER_NOT_FOUND("회원이 확인되지 않습니다."),
	MEMBER_SIGNUP_FAILED("회원가입 실패하였습니다.");

	private final String msg;

	CommonExceptionEnum(String msg) {
		this.msg = msg;
	}
}
