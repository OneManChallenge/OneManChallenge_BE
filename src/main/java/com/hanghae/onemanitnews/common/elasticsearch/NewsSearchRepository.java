package com.hanghae.onemanitnews.common.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

// TODO changed id, rollback after plz...
public interface NewsSearchRepository extends ElasticsearchRepository<NewsDocument, Long> {
	Page<NewsDocument> findAllByTitleContains(String search, Pageable pageable);

	Page<NewsDocument> findAllByContentContains(String search, Pageable pageable);
}
