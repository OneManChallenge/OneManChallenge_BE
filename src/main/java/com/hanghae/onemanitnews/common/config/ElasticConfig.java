package com.hanghae.onemanitnews.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

@EnableElasticsearchRepositories
@Configuration
public class ElasticConfig extends ElasticsearchConfiguration {

	@NonNull
	@Override
	public ClientConfiguration clientConfiguration() {
		var builder = ClientConfiguration.builder()
			.connectedTo("172.30.1.1:9200");

		return builder.build();
	}
}
