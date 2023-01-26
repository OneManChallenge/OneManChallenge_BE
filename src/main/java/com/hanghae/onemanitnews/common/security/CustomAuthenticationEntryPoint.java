package com.hanghae.onemanitnews.common.security;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.onemanitnews.common.exception.CommonExceptionEnum;
import com.hanghae.onemanitnews.common.response.FailResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint { //토큰 인증 예외처리
	// 인증과정에서 실패하거나 인증헤더(Authorization)를 보내지 않게되는 경우 401(UnAuthorized) 라는 응답값을 받게되는데요.
	// 인증되지 않은 사용자의 리소스에 대한 접근 처리는 AuthenticationEntryPoint가 담당
	// 이를 처리해주는 로직이 바로 AuthenticationEntryPoint라는 인터페이스
	// Response에 401이 떨어질만한 에러가 발생할 경우 해당로직을 타게되어, commence라는 메소드를 실행

	@Override
	public void commence(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authenticationException) throws IOException {

		FailResponse responseDto = FailResponse.builder()
			.msg(CommonExceptionEnum.TOKEN_ENTRY_POINT_ERROR.getMsg())
			.build();

		log.info("CustomAuthenticationEntryPoint 예외발생!");

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		try (OutputStream os = response.getOutputStream()) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(os, responseDto);
			os.flush();
		}
	}

	/*
	 * 예외 문자열 발송 예제
	 * response.setContentType("application/json; charset=utf8");
	 * response.getWriter().println("Oh my god!! 당신 토큰이 좀 이상한거 같다?");
	 */

}
