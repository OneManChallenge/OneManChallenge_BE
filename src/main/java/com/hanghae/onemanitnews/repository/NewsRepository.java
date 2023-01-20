package com.hanghae.onemanitnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanghae.onemanitnews.entity.News;

public interface NewsRepository extends JpaRepository<News, Long> {
}
