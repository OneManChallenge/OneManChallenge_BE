package com.hanghae.onemanitnews.common.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
	private final CommonExceptionEnum msg;

	public CommonException(CommonExceptionEnum errorEnum) {
		this.msg = errorEnum;
	}
}
