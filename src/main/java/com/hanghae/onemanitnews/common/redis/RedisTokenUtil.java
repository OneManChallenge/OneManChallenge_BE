package com.hanghae.onemanitnews.common.redis;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.google.common.hash.Hashing;
import com.hanghae.onemanitnews.common.jwt.JwtAccessUtil;
import com.hanghae.onemanitnews.common.jwt.JwtRefreshUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisTokenUtil {
	public final RedisTemplate<String, String> redisTemplate;
	private final JwtAccessUtil jwtAccessUtil;
	private final JwtRefreshUtil jwtRefreshUtil;

	/** Http request 헤더 내 Access 토큰 추출 **/
	public String getHeaderAccessToken(HttpServletRequest request) {
		String accessToken = request.getHeader(jwtAccessUtil.ACCESS_HEADER);

		if (accessToken != null) {
			return accessToken;
		}
		return null;
	}

	/** Http request 헤더 내 Refresh 토큰 추출 **/
	public String getHeaderRefreshToken(HttpServletRequest request) {
		String refreshToken = request.getHeader(jwtRefreshUtil.REFRESH_HEADER);

		if (refreshToken != null) {
			return refreshToken;
		}
		return null;
	}

	/** Redis Key Hash 변환 전용 메서드(salt 기능 추가) **/
	public String createRedisHash(String hashTarget, RedisHashEnum redisHashEnum) {

		String saltedHashTarget = "";

		if (redisHashEnum.equals(RedisHashEnum.ACCESS_TOKEN)) {
			saltedHashTarget = hashTarget + "OnemanAccess";
		}

		if (redisHashEnum.equals(RedisHashEnum.REFRESH_TOKEN)) {
			saltedHashTarget = hashTarget + "OnemanRefresh";
		}

		if (redisHashEnum.equals(RedisHashEnum.EMAIL)) {
			saltedHashTarget = hashTarget + "OnemanEmail";
		}

		return String.valueOf(Hashing.murmur3_32().hashString(saltedHashTarget, StandardCharsets.UTF_8).asInt());
	}

	/** Redis Access Token Key-value 검사 **/
	public boolean isAccessTokenValid(String accessToken) {
		String saltedAccessToken = accessToken + "OnemanAccess";

		String accessHash = String.valueOf(
			Hashing.murmur3_32().hashString(saltedAccessToken, StandardCharsets.UTF_8).asInt());

		if (redisTemplate.opsForValue().get(accessHash) != null) {
			return true;
		}
		return false;
	}

	/** Redis Refresh Token Key-value 검사 **/
	public boolean isRefreshTokenValid(String refreshToken) {
		String saltedRefreshToken = refreshToken + "OnemanRefresh";

		String refreshsHash = String.valueOf(
			Hashing.murmur3_32().hashString(saltedRefreshToken, StandardCharsets.UTF_8).asInt());

		if (redisTemplate.opsForValue().get(refreshsHash) != null) {
			return true;
		}
		return false;
	}
}
