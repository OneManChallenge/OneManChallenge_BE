package com.hanghae.onemanitnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanghae.onemanitnews.entity.Subscribe;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
}
