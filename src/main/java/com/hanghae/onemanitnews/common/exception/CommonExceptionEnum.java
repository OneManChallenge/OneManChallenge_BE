package com.hanghae.onemanitnews.common.exception;

import lombok.Getter;

@Getter
public enum CommonExceptionEnum {
	/* News 예외 메시지 */
	NEWS_NOT_FOUND_MSG("뉴스가 검색되지 않습니다."),

	/* Member 예외 메시지 */
	DUPLICATE_EMAIL("이미 가입한 Email입니다."),
	MEMBER_NOT_FOUND("회원이 확인되지 않습니다."),
	MEMBER_SIGNUP_FAILED("회원가입 실패하였습니다."),
	INCORRECT_PASSWORD("비밀번호가 일치하지 않습니다."),

	NODE_JS_COUNT_FAIL("Node.js 서버 Error!!"),

	/* JWT 예외 메시지 */
	TOKEN_ENTRY_POINT_ERROR("유효하지 않은 토큰입니다."),
	TOKEN_DO_FILTER_INTERNAL_ERROR("토큰 검증 과정에서 오류가 있습니다."),
	EXPIRED_REFRESH_TOKEN("Re-fresh 토큰이 유효하지 않습니다. 다시 로그인해주세요."),
	NO_REFRESH_TOKEN("Re-fresh 토큰이 확인되지 않습니다."),
	NO_ACCESS_TOKEN("Access 토큰이 확인되지 않습니다.");
	private final String msg;

	CommonExceptionEnum(String msg) {
		this.msg = msg;
	}
}
