package com.hanghae.onemanitnews.controller.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FindNewsResponse {
	private Long newsId;

	private String division;

	private String title;

	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime articleDate;

	private String imgUrl;

	private String mainUrl;

	private Long views;
}
