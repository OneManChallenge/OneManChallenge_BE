package com.hanghae.onemanitnews.common.redis;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.onemanitnews.common.exception.CommonExceptionEnum;
import com.hanghae.onemanitnews.common.jwt.JwtAccessUtil;
import com.hanghae.onemanitnews.common.jwt.JwtRefreshUtil;
import com.hanghae.onemanitnews.common.response.FailResponse;
import com.hanghae.onemanitnews.common.security.dto.MemberRoleEnum;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RedisAuthFilter extends OncePerRequestFilter {

	private final RedisTokenUtil redisTokenUtil;
	private final RedisTemplate<String, String> redisTemplate;
	private final JwtAccessUtil jwtAccessUtil;
	private final JwtRefreshUtil jwtRefreshUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		//1. 헤더에서 토큰 가져오기
		String accessToken = redisTokenUtil.getHeaderAccessToken(request);
		String refreshToken = redisTokenUtil.getHeaderRefreshToken(request);

		//2. 토큰 2개 가지고 있는 경우 - 검사 대상
		if (accessToken != null & refreshToken != null) {

			//3. Access/Refresh 토큰 유효성 검사
			//3-1. Redis 토큰 검사
			boolean isAccess = redisTokenUtil.isAccessTokenValid(accessToken);
			boolean isRefresh = redisTokenUtil.isRefreshTokenValid(refreshToken);

			//3-1. Access/Refresh 둘다 유효 할 경우 -  인증객체 생성 후 doFilter
			if (isAccess & isRefresh) {
				String accessJWT = jwtAccessUtil.resolveAccessToken(request);
				Claims accessInfo = jwtAccessUtil.getMemberInfoFromAccessToken(accessJWT);
				if (accessJWT == null) {
					sendFailureMessage(CommonExceptionEnum.NO_ACCESS_TOKEN, response);
					return;
				}
				setAccessAuthentication(accessInfo.getSubject());
			}

			//3-2. Redis에서 Access X, Refresh O - 헤더에 있던 Access토큰 검사 후 재발행 또는 예외발생
			if (!isAccess & isRefresh) {
				//3-2-1. 헤더 내 Refresh 토큰 검사
				String refreshJWT = jwtRefreshUtil.resolveRefreshToken(request);
				Claims refreshInfo = jwtRefreshUtil.getMemberInfoFromRefreshToken(refreshJWT);

				if (refreshJWT == null) {
					sendFailureMessage(CommonExceptionEnum.NO_REFRESH_TOKEN, response);
					return;
				}

				if (!jwtRefreshUtil.validateRefreshToken(refreshJWT)) {
					sendFailureMessage(CommonExceptionEnum.EXPIRED_REFRESH_TOKEN, response);
					return;
				}

				//3-2-2. 헤더 내 Access 토큰 검사
				accessToken = jwtAccessUtil.resolveAccessToken(request);
				String accessTokenCheck = jwtAccessUtil.validateAccessToken(accessToken);

				//3-2-1. Access토큰 재발급 진행 -헤더에 있던 Access토큰 정상이고 만료되었고, Refresh 토큰 유효할 경우
				if (accessTokenCheck.equals("Refresh")) {
					//3-2-1-1. Access 토큰 재발행 후 http 헤더에 저장
					accessToken = jwtAccessUtil.createAccessToken(refreshInfo.getSubject(), MemberRoleEnum.ONEMAN);
					response.addHeader(jwtAccessUtil.ACCESS_HEADER, accessToken);

					//3-2-1-2. Redis 내 재발행된 Access토큰 내용 반영
					String emailHash = redisTokenUtil.createRedisHash(refreshInfo.getSubject(), RedisHashEnum.EMAIL);
					String accessHash = redisTokenUtil.createRedisHash(emailHash, RedisHashEnum.ACCESS_TOKEN);

					redisTemplate.opsForHash().put(emailHash, "accessToken", accessHash);
					redisTemplate.opsForValue()
						.set(accessHash, "re-available", jwtAccessUtil.ACCESS_TOKEN_TIME, TimeUnit.MILLISECONDS);
				}

				//3-3. 유효하지 않은 Access 토큰 인 경우 예외 발생
				if (accessTokenCheck.equals("NO")) {
					sendFailureMessage(CommonExceptionEnum.TOKEN_DO_FILTER_INTERNAL_ERROR, response);
					return;
				}
				setAccessAuthentication(refreshInfo.getSubject()); //인증객체 생성
			}

			//4. Redis 내 토큰 정보가 아무것도 없을 경우 재로그인 안내
			if (!isAccess & !isRefresh) {
				sendFailureMessage(CommonExceptionEnum.EXPIRED_REFRESH_TOKEN, response);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	/** 인증객체 생성 메서드 - SercurityContextHolder 안에 등록 - jwt**/
	public void setAccessAuthentication(String email) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = jwtAccessUtil.createAuthentication(email);
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