package com.hanghae.onemanitnews.common.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hanghae.onemanitnews.common.security.CustomAuthenticationEntryPoint;
import com.hanghae.onemanitnews.common.security.jwt.JwtAccessUtil;
import com.hanghae.onemanitnews.common.security.jwt.JwtAuthFilter;
import com.hanghae.onemanitnews.common.security.jwt.JwtRefreshUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
	private final JwtAccessUtil jwtAccessUtil;
	private final JwtRefreshUtil jwtRefreshUtil;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	} //암호강도 12설정(기본 10, 최소 4, 최대 31)

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// h2-console 사용 및 resources 접근 허용 설정 => 정적 리소스 필터 제외하여 서버 부하 줄임
		return (web) -> web.ignoring()
			//.requestMatchers(PathRequest.toH2Console())
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF(Cross-site request forgery) 비활성화 설정
		// 공격자가 인증된 브라우저에 저장된 쿠키의 세션 정보를 활용하여 웹 서버에 사용자가 의도하지 않은 요청을 전달하는 것
		http.csrf().disable();

		// Session 방식 비활성화
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// .authorizeRequests() : 요청에 대한 권한을 지정
		http.authorizeRequests()
			//.antMatchers(HttpMethod.GET, "/api/v1/news/list").permitAll()
			.antMatchers(HttpMethod.POST, "/api/v1/member/signup").permitAll()
			.antMatchers(HttpMethod.POST, "/api/v1/member/login").permitAll()
			.anyRequest().authenticated(); //나머진 토큰 필요

		// JWT Filter 등록 - UPAF필터보다 먼저 적용
		http.addFilterBefore(new JwtAuthFilter(jwtAccessUtil, jwtRefreshUtil),
			UsernamePasswordAuthenticationFilter.class);
		// http.addFilterBefore(new JwtExceptionHandlerFilter(), JwtAuthFilter.class);

		// JWT Filter를 통과한 이후, 어떤 이유로 인해 토큰 만료 됐을 때 검증(ExceptionTranslationFilter)
		// 401 Error, 인증 실패
		http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);

		// 403 Error, 권한 오류
		// http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);

		return http.build();
	}
}
