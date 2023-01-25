package com.hanghae.onemanitnews.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	} //암호강도 12설정(기본 10, 최소 4, 최대 31)

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF(Cross-site request forgery) 비활성화 설정
		// 공격자가 인증된 브라우저에 저장된 쿠키의 세션 정보를 활용하여 웹 서버에 사용자가 의도하지 않은 요청을 전달하는 것
		http.csrf().disable();

		// Session 방식 비활성화
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// .authorizeRequests() : 요청에 대한 권한을 지정
		http.authorizeRequests()
			.antMatchers("/api/v1/news/list").permitAll()
			.antMatchers("/api/v1/member/signup").permitAll()
			.anyRequest().authenticated(); //나머진 토큰 필요

		return http.build();
	}
}
