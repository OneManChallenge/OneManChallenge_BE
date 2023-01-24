package com.hanghae.onemanitnews.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.onemanitnews.common.response.SuccessResponse;
import com.hanghae.onemanitnews.service.NewsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
@RestController
public class NewsApiController {

	private final NewsService newsService;
	
	//1. 뉴스 검색 기능 - 페이징 + Valid
	@GetMapping("/list")
	public ResponseEntity<?> getNewsList(
		@RequestParam("category") @Range(min = 1, max = 2, message = "올바르지 않은 카테고리입니다.") int category,
		@RequestParam("search") @NotBlank(message = "검색어를 입력해주세요.") String search,
		@RequestParam("page") @Positive(message = "페이지는 양수만 가능합니다.") int page) {

		return new ResponseEntity<>(SuccessResponse.builder()
			.msg("뉴스검색 성공")
			.data(newsService.getNewsList(category, search, page - 1))
			.build(),
			HttpStatus.OK);
	}
}
