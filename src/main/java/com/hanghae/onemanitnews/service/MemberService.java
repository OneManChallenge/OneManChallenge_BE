package com.hanghae.onemanitnews.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.onemanitnews.common.exception.CommonException;
import com.hanghae.onemanitnews.common.exception.CommonExceptionEnum;
import com.hanghae.onemanitnews.common.security.dto.MemberRoleEnum;
import com.hanghae.onemanitnews.common.security.jwt.JwtAccessUtil;
import com.hanghae.onemanitnews.common.security.jwt.JwtRefreshUtil;
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
			memberRepository.save(member);
		} catch (Exception e) {
			throw new CommonException(CommonExceptionEnum.MEMBER_SIGNUP_FAILED);
		}
	}

	@Transactional(readOnly = true)
	public void login(LoginMemberRequest loginMemberRequest, HttpServletResponse response) {
		/* 1. 로그인 관련 필드 준비 */
		String email = loginMemberRequest.getEmail();
		String password = loginMemberRequest.getPassword();

		//1. 회원 존재 여부 확인
		Member member = memberRepository.findByEmail(email).orElseThrow(
			() -> new CommonException(CommonExceptionEnum.MEMBER_NOT_FOUND)
		);

		//2. 비밀번호 일치여부 확인
		if (!passwordEncoder.matches(password, member.getPassword())) {
			throw new CommonException(CommonExceptionEnum.INCORRECT_PASSWORD);
		}

		//3. Access JWT생성 및 발급
		response.addHeader(jwtAccessUtil.ACCESS_HEADER,
			jwtAccessUtil.createAccessToken(email, MemberRoleEnum.ONEMAN)); //AccessToken

		//4. Refresh JWT생성 및 발급
		response.addHeader(jwtRefreshUtil.REFRESH_HEADER,
			jwtRefreshUtil.createRefreshToken(email, MemberRoleEnum.ONEMAN)); //RefreshToken
	}
}