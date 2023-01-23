package com.hanghae.onemanitnews.common.elasticsearch;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(indexName = "news")
public class NewsDocument {

	/** 여기가 진짜 elasticsearch id */
	// @Id
	// @Field(type = FieldType.Keyword)
	// private String documentId;

	/** 여기는 elasticsearch id가 아님에 주의 */
	@Id
	@Field(type = FieldType.Keyword, name = "news_id")
	private Long newsId;

	@Field(type = FieldType.Text)
	private String division;

	@Field(type = FieldType.Text)
	private String title;

	@Field(type = FieldType.Text)
	private String content;

	@Field(type = FieldType.Date, name = "article_date")
	private LocalDateTime articleDate;

	@Field(type = FieldType.Text, name = "img_url")
	private String imgUrl;

	@Field(type = FieldType.Text, name = "main_url")
	private String mainUrl;

	@Field(type = FieldType.Long)
	private Long views;

	@Field(type = FieldType.Boolean)
	private Boolean deleted;

	@Field(type = FieldType.Date, name = "modification_date")
	private LocalDateTime modificationDate;

	@Builder
	public NewsDocument(Long newsId, String division, String title,
		String content, LocalDateTime articleDate, String imgUrl,
		String mainUrl, Long views) {
		this.newsId = newsId;
		this.division = division;
		this.title = title;
		this.content = content;
		this.articleDate = articleDate;
		this.imgUrl = imgUrl;
		this.mainUrl = mainUrl;
		this.views = views;
	}
}