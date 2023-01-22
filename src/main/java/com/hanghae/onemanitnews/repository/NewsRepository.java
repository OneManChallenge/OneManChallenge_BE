package com.hanghae.onemanitnews.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hanghae.onemanitnews.entity.News;

public interface NewsRepository extends JpaRepository<News, Long> {
	Page<News> findAllByTitleContains(String search, Pageable pageable);

	Page<News> findAllByContentContains(String search, Pageable pageable);
}
