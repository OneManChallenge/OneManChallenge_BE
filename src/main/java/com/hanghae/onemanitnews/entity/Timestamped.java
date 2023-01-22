package com.hanghae.onemanitnews.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Timestamped {
	@CreatedDate
	@Column(name = "insertion_date", nullable = true)
	private LocalDateTime insertionDate;

	@LastModifiedDate
	@Column(name = "modification_date", nullable = true)
	private LocalDateTime modificationDate;

	/**
	 @PrePersist : 해당 엔티티 저장하기 전 실행
	 - onPrePersist() : 생성일자 내 나노초 제거함
	 사용 전 : 2022-12-25T01:23:53.73487
	 사용 후 : 2022-12-25T01:23:53
	 */
	@PrePersist
	private void onPrePersist() {
		this.insertionDate = LocalDateTime.now().withNano(0); //나노초 9자 제거
		this.modificationDate = LocalDateTime.now().withNano(0);
	}
}
