package com.hanghae.onemanitnews.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class SuccessResponse<T> {
	private String result;
	private String msg;
	private T data;

	@Builder
	public SuccessResponse(String msg, T data) {
		this.result = "success";
		this.msg = msg;
		this.data = data;
	}
}
