package com.hanghae.onemanitnews.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.onemanitnews.common.exception.CommonException;
import com.hanghae.onemanitnews.common.exception.CommonExceptionEnum;
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
}