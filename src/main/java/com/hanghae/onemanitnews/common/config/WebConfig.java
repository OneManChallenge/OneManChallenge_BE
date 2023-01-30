package com.hanghae.onemanitnews.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry reg) {
		reg.addMapping("/api/v1/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "PATCH")
			.allowCredentials(false)
			.maxAge(3000)
			.exposedHeaders("Authorization", "OneManToken") //이걸 해줘야 Front에서 토큰 값 가져오기 가능
		;

	}
}
