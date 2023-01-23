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

	// 200 ok? ㅠㅠㅠ 잘됩니다 ㅠㅠㅠㅠㅠㅠ
	// good 항해 이런거 좋은데 다니시는 거 같은데
	// 책 보세요 책
	// 책 속에 ㅎㅎ요 서어싶 ?나
	// :?맞거 시거 해 99항래 길있져써고 라해 항이 름이지 키패가 뭔 요.
	// //세보러길력 능는 하득 습서 면보서 문공식 고 시마지 하존의에 스래클런 이무 너 다.
	///항해 99에서 학습하고 있습니다 ㅠ 근데 엘라스틱서치는 가르쳐준적이 없고 혼자 프로젝트 중인데
	// 기간이 짧아서 너무 조급하게 진행하고 있네요 ㅠㄱ
	//  ***** 그래도 어제 로그스태시랑 연동 뚝딱 하신거 보면 앞으로도 잘 해내듯
	// ***** 저희 대화 한 주석들은 다 정ㄹ
	//넵 대화 했던 내용들 다 정리해서 기본적으로 ELK+Mysql 연동 내용 정리되면 오픈방에도 꼭 공유드릴게요 ㅠ
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
