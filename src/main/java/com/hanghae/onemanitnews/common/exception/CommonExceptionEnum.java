package com.hanghae.onemanitnews.common.exception;

import lombok.Getter;

@Getter
public enum CommonExceptionEnum {
	/* Auth 예외 메시지 */
	EMAIL_NOT_FOUND_MSG("해당 이메일이 존재하지 않습니다."),
	PASSWORD_NOT_MATCH_MSG("비밀번호가 일치하지 않습니다."),
	DUPLICATED_EMAIL_MSG("중복된 이메일입니다."),
	MEMBER_NOT_FOUND_MSG("유효하지 않은 회원입니다.")

	/* 투두 예외 메시지 */,
	TODO_NOT_FOUND_MSG("Todo를 찾을 수 없습니다."),
	TODO_INVALID_PERMISSION_MSG("Todo 권한이 유효하지 않습니다."),

	/* Tag 예외 메시지 */
	TAG_NOT_FOUND_MSG("Tag를 찾을 수 없습니다."),
	TAG_INVALID_PERMISSION_MSG("Tag 권한이 유효하지 않습니다."),
	TAG_NOT_DELETE_MSG("태그를 삭제 할 수 없습니다."),

	/* JWT 예외 메시지 */
	TOKEN_NOT_FOUND_MSG("토큰이 존재하지 않습니다."),
	INVALID_TOKEN_MSG("토큰이 유효하지 않습니다.");

	private final String msg;

	CommonExceptionEnum(String msg) {
		this.msg = msg;
	}
}
