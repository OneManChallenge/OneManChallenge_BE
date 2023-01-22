package com.hanghae.onemanitnews.common.exception;

import lombok.Getter;

@Getter
public enum CommonExceptionEnum {
	/* News 예외 메시지 */
	NEWS_NOT_FOUND_MSG("뉴스가 검색되지 않습니다.");

	private final String msg;

	CommonExceptionEnum(String msg) {
		this.msg = msg;
	}
}
