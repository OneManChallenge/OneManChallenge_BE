package com.hanghae.onemanitnews.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FailResponse {
	private String result;
	private String msg;

	@Builder
	public FailResponse(String msg) {
		this.result = "fail";
		this.msg = msg;
	}
}
