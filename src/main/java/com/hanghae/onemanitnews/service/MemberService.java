package com.hanghae.onemanitnews.service;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.onemanitnews.common.exception.CommonException;
import com.hanghae.onemanitnews.common.exception.CommonExceptionEnum;
import com.hanghae.onemanitnews.common.jwt.JwtAccessUtil;
import com.hanghae.onemanitnews.common.jwt.JwtRefreshUtil;
import com.hanghae.onemanitnews.common.redis.RedisHashEnum;
import com.hanghae.onemanitnews.common.redis.RedisTokenUtil;
import com.hanghae.onemanitnews.common.security.dto.MemberRoleEnum;
import com.hanghae.onemanitnews.controller.request.LoginMemberRequest;
import com.hanghae.onemanitnews.controller.request.SaveMemberRequest;
import com.hanghae.onemanitnews.entity.Member;
import com.hanghae.onemanitnews.mapper.MemberMapper;
import com.hanghae.onemanitnews.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtAccessUtil jwtAccessUtil;
	private final JwtRefreshUtil jwtRefreshUtil;
	private final RedisTemplate<String, String> redisTemplate;

	private final RedisTokenUtil redisTokenUtil;

	@Transactional
	public void signup(SaveMemberRequest saveMemberRequest) {
		//1. 중복 가입 유무 체크
		Boolean isEmail = memberRepository.existsByEmail(saveMemberRequest.getEmail());

		if (isEmail == true) {
			throw new CommonException(CommonExceptionEnum.DUPLICATE_EMAIL);
		}

		//2. 비밀번호 암호화
		String encryptPassword = passwordEncoder.encode(saveMemberRequest.getPassword());

		//3. Dto -> Entity
		Member member = memberMapper.toEntity(saveMemberRequest.getEmail(), encryptPassword);

		//4. 회원가입
		try {
			memberRepository.last_increment_id(member.getEmail(), encryptPassword);
		} catch (Exception e) {
			throw new CommonException(CommonExceptionEnum.MEMBER_SIGNUP_FAILED);
		}
	}

	@Transactional(readOnly = true)
	public void login(LoginMemberRequest loginMemberRequest, HttpServletResponse response) {
		/** 1. 필드 준비 **/
		String email = loginMemberRequest.getEmail();
		String password = loginMemberRequest.getPassword();
		final String HASH_KEY_ACCESS_TOKEN = "accessToken";
		final String HASH_KEY_REFRESH_TOKEN = "refreshToken";

		/** 2. 회원 존재 여부 확인 **/
		Member member = memberRepository.findByEmail(email).orElseThrow(
			() -> new CommonException(CommonExceptionEnum.MEMBER_NOT_FOUND)
		);

		/** 3. 비밀번호 일치여부 확인 **/
		if (!passwordEncoder.matches(password, member.getPassword())) {
			throw new CommonException(CommonExceptionEnum.INCORRECT_PASSWORD);
		}

		/** 3. JWT 생성 및 발급 **/
		// 3-1. Access/Refresh JWT 생성
		String accessToken = jwtAccessUtil.createAccessToken(email, MemberRoleEnum.ONEMAN);
		String refreshToken = jwtRefreshUtil.createRefreshToken(email, MemberRoleEnum.ONEMAN);

		// 3-2. 생성된 JWT를 Http Response 헤더에 추가
		response.addHeader(jwtAccessUtil.ACCESS_HEADER, accessToken);
		response.addHeader(jwtRefreshUtil.REFRESH_HEADER, refreshToken);

		/** 4. Redis로 해시된 토큰 Set **/
		//4-1. key hash 암호화(MurmurHash)
		String emailHash = redisTokenUtil.createRedisHash(email, RedisHashEnum.EMAIL);
		String accessHash = redisTokenUtil.createRedisHash(accessToken, RedisHashEnum.ACCESS_TOKEN);
		String refreshHash = redisTokenUtil.createRedisHash(refreshToken, RedisHashEnum.REFRESH_TOKEN);

		//4-2. Redis Hash Key 조회 - 기존 사용 중인 토큰정보 있는 Key 조회 및 삭제
		Object redisHgetAccessToken = redisTemplate.opsForHash().get(emailHash, HASH_KEY_ACCESS_TOKEN);
		Object redisHgetRefreshToken = redisTemplate.opsForHash().get(emailHash, HASH_KEY_REFRESH_TOKEN);

		if (redisHgetRefreshToken != null) { // 4-3의 이유로 생략 : redisHgetAccessToken != null
			redisTemplate.delete(redisHgetAccessToken.toString());
			redisTemplate.delete(redisHgetRefreshToken.toString());
		}

		//4-3. 새로운 Redis Hash key-value 저장 - 무제한 생성 방지용 - 유효시간은 refresh 토큰 시간
		redisTemplate.opsForHash().put(emailHash, HASH_KEY_ACCESS_TOKEN, accessHash);
		redisTemplate.opsForHash().put(emailHash, HASH_KEY_REFRESH_TOKEN, refreshHash);
		redisTemplate.expire(emailHash, jwtRefreshUtil.REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);

		//4-4. 새로 발급된 AccessToken/RefreshToken Redis 저장
		ValueOperations<String, String> vop = redisTemplate.opsForValue();
		vop.set(accessHash, "available", jwtAccessUtil.ACCESS_TOKEN_TIME, TimeUnit.MILLISECONDS);
		vop.set(refreshHash, "available", jwtRefreshUtil.REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);
	}

	@Transactional
	public void logout(UserDetails userDetails, HttpServletRequest request) {
		//1. 헤더에서 토큰 가져오기
		String accessToken = redisTokenUtil.getHeaderAccessToken(request);
		String refreshToken = redisTokenUtil.getHeaderRefreshToken(request);

		//2. Redis key 삭제를 위한 hash 암호화(MurmurHash)
		String emailHash = redisTokenUtil.createRedisHash(userDetails.getUsername(), RedisHashEnum.EMAIL);
		String accessHash = redisTokenUtil.createRedisHash(accessToken, RedisHashEnum.ACCESS_TOKEN);
		String refreshHash = redisTokenUtil.createRedisHash(refreshToken, RedisHashEnum.REFRESH_TOKEN);

		//3. Redis Key 삭제
		redisTemplate.delete(emailHash);
		redisTemplate.delete(accessHash);
		redisTemplate.delete(refreshHash);
	}
}