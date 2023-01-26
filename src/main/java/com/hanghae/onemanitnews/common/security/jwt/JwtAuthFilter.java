package com.hanghae.onemanitnews.common.security.jwt;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.onemanitnews.common.exception.CommonExceptionEnum;
import com.hanghae.onemanitnews.common.response.FailResponse;
import com.hanghae.onemanitnews.common.security.dto.MemberRoleEnum;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtAccessUtil jwtAccessUtil;
	private final JwtRefreshUtil jwtRefreshUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		//1. header에서 AccessToken 가져오기
		String accessToken = jwtAccessUtil.resolveAccessToken(request);  // 2. header에서 AccessToken 가져옴

		//2. Access 토큰 검증
		if (accessToken != null) { //회원가입, 로그인 시에는 토큰이 없기에 조건문 달아야함
			String accessTokenCheck = jwtAccessUtil.validateAccessToken(accessToken);

			if (accessTokenCheck.equals("Access")) {
				Claims info = jwtAccessUtil.getMemberInfoFromAccessToken(
					accessToken); //5. 토큰에서 사용자 정보 가져오기, getBody()를 통해 사용자 정보 가져옴
				setAccessAuthentication(info.getSubject()); //getSubject()로 ID값 가져와서 검증.. 아래 45번줄 setAuthentication()
			}

			if (accessTokenCheck.equals("Refresh")) {
				String refreshToken = jwtRefreshUtil.resolveRefreshToken(request);
				if (refreshToken == null) {
					sendFailureMessage(CommonExceptionEnum.NO_REFRESH_TOKEN, response);
					return;
				}

				if (!jwtRefreshUtil.validateRefreshToken(refreshToken)) {
					sendFailureMessage(CommonExceptionEnum.EXPIRED_REFRESH_TOKEN, response);
					return;
				}

				Claims info = jwtRefreshUtil.getMemberInfoFromRefreshToken(refreshToken);

				accessToken = jwtAccessUtil.createAccessToken(info.getSubject(), MemberRoleEnum.ONEMAN);
				response.addHeader(jwtAccessUtil.ACCESS_HEADER, accessToken);

				setAccessAuthentication(info.getSubject());
			}

			if (accessTokenCheck.equals("NO")) { //JwtUtil 안에 4. 토큰 검증
				sendFailureMessage(CommonExceptionEnum.TOKEN_DO_FILTER_INTERNAL_ERROR, response);
				return; //이거 없으면 콘솔창에 에러 터진거 막 출력됨
			}
		}
		filterChain.doFilter(request, response);
	}

	// 인증객체 생성하고 SercurityContextHolder 안에 등록
	public void setAccessAuthentication(String email) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = jwtAccessUtil.createAuthentication(email); //JwtUtil 안에 만든 - 인증 객체 생성
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}

	public void sendFailureMessage(CommonExceptionEnum exceptionEnum, HttpServletResponse response) throws IOException {
		//유효하지 않은 토큰 예외 발생
		FailResponse responseDto = new FailResponse(exceptionEnum.getMsg());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		try (OutputStream os = response.getOutputStream()) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(os, responseDto);
			os.flush();
		}
	}
}
