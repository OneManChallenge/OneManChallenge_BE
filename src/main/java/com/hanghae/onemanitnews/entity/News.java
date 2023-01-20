package com.hanghae.onemanitnews.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;

@SQLDelete(sql = "UPDATE news SET deleted = true WHERE news_id = ?") //soft delete 적용
@Where(clause = "deleted = false") //soft delete 적용하여, 조회 시 자동으로 delete = false 조건절 추가
@Getter
@Entity
public class News {

	@Column(name = "news_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long newsId;

	@Column(nullable = false, length = 50)
	private String division;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false) //length = 255
	private String content;

	@Column(nullable = false, name = "article_date")
	private LocalDateTime articleDate;

	@Column(nullable = false, length = 100)
	private String imgUrl;

	@Column(nullable = false, length = 100)
	private String mainUrl;

	@Column(nullable = false)
	private Long views = 0L; //조회수 기본값 0

	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE; //삭제유무 기본값 false
}
