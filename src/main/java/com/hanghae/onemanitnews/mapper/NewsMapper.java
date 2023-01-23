package com.hanghae.onemanitnews.mapper;

import org.springframework.stereotype.Component;

import com.hanghae.onemanitnews.common.elasticsearch.NewsDocument;
import com.hanghae.onemanitnews.controller.response.FindNewsResponse;
import com.hanghae.onemanitnews.entity.News;

@Component
public class NewsMapper {

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

	public FindNewsResponse toNewsDocDto(NewsDocument newsDoc) {
		return FindNewsResponse.builder()
			.newsId(newsDoc.getNewsId())
			.division(newsDoc.getDivision())
			.title(newsDoc.getTitle())
			.content(newsDoc.getContent())
			.articleDate(newsDoc.getArticleDate())
			.imgUrl(newsDoc.getImgUrl())
			.mainUrl(newsDoc.getMainUrl())
			.views(newsDoc.getViews())
			.build();
	}
}

