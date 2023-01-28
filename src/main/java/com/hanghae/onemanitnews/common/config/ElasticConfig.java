package com.hanghae.onemanitnews.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

@EnableElasticsearchRepositories
@Configuration
public class ElasticConfig extends ElasticsearchConfiguration {

	@Value("${elasticsearch.hostAndPort}")
	private String hostAndPort;

	@NonNull
	@Override
	public ClientConfiguration clientConfiguration() {
		var builder = ClientConfiguration.builder()
			.connectedTo(hostAndPort);

		return builder.build();
	}
}
