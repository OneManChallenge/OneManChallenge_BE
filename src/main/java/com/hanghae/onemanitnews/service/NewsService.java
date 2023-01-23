package com.hanghae.onemanitnews.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.onemanitnews.common.elasticsearch.NewsDocument;
import com.hanghae.onemanitnews.common.elasticsearch.NewsSearchRepository;
import com.hanghae.onemanitnews.common.exception.CommonException;
import com.hanghae.onemanitnews.common.exception.CommonExceptionEnum;
import com.hanghae.onemanitnews.controller.response.FindNewsResponse;
import com.hanghae.onemanitnews.mapper.NewsMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NewsService {
	//private final NewsRepository newsRepository;
	private final NewsSearchRepository newsSearchRepository;
	private final NewsMapper newsMapper;

	/** 1. 뉴스 검색 **/
	@Transactional(readOnly = true)
	public List<FindNewsResponse> getNewsList(int category, String search, int page) {
		//1. 뉴스 검색 필드 준비
		final int NEWS_PAGE_SIZE = 10;

		Sort.Direction direction = Sort.Direction.DESC;
		Sort sort = Sort.by(direction, "articleDate");
		Pageable pageable = PageRequest.of(page, NEWS_PAGE_SIZE, sort);

		//2. 뉴스 조회
		// Page<News> newsPage = (category == 1) ?
		// 	newsRepository.findAllByTitleContains(search, pageable) :
		// 	newsRepository.findAllByContentContains(search, pageable);

		Page<NewsDocument> newsPage = (category == 1) ? // 조회 되요.
			newsSearchRepository.findAllByTitleContains(search, pageable) :
			newsSearchRepository.findAllByContentContains(search, pageable);

		if (newsPage.getContent().isEmpty()) {
			throw new CommonException(CommonExceptionEnum.NEWS_NOT_FOUND_MSG);
		}

		//3. Entity -> Dto
		List<FindNewsResponse> newsListDto = new ArrayList<>();

		// for (News news : newsPage.getContent()) {
		// 	newsListDto.add(newsMapper.toNewsDto(news));
		// }

		for (NewsDocument newsDoc : newsPage.getContent()) {
			newsListDto.add(newsMapper.toNewsDocDto(newsDoc));
		}

		//4. 결과 반환
		return newsListDto;
	}
}
