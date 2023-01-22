package com.hanghae.onemanitnews.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
public class Subscribe {
	@Column(name = "subscribe_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long subscribeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "news_id", nullable = false)
	private News news;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@CreatedDate
	@Column(name = "insertion_date", nullable = false)
	private LocalDateTime insertionDate;

	@PrePersist
	private void onPrePersist() {
		this.insertionDate = LocalDateTime.now().withNano(0); //나노초 9자 제거
	}
}
