package com.hanghae.onemanitnews.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE news SET deleted = true WHERE news_id = ?") //soft delete 적용
@Where(clause = "deleted = false") //soft delete 적용하여, 조회 시 자동으로 delete = false 조건절 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@Column(nullable = false, name = "img_url", length = 300)
	private String imgUrl;

	@Column(nullable = false, name = "main_url", length = 100)
	private String mainUrl;

	@Column(nullable = false)
	private Long views = 0L; //조회수 기본값 0

	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE; //삭제유무 기본값 false

	@LastModifiedDate
	@Column(name = "modification_date", nullable = false) //Logstash 동기화 위해 사용
	private LocalDateTime modificationDate;

	@PrePersist
	private void onPrePersist() {
		this.modificationDate = LocalDateTime.now().withNano(0);
	}

	@Builder
	public News(Long newsId, String division, String title,
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
