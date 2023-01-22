package com.hanghae.onemanitnews.mapper;

import org.springframework.stereotype.Component;

import com.hanghae.onemanitnews.controller.response.FindNewsResponse;
import com.hanghae.onemanitnews.entity.News;

import lombok.Builder;

@Component
public class NewsMapper {
	@Builder
	public FindNewsResponse toNewsDto(News news) {
		return FindNewsResponse.builder()
			.newsId(news.getNewsId())
			.division(news.getDivision())
			.title(news.getTitle())
			.content(news.getContent())
			.articleDate(news.getArticleDate())
			.imgUrl(news.getImgUrl())
			.mainUrl(news.getMainUrl())
			.views(news.getViews())
			.build();
	}
}
