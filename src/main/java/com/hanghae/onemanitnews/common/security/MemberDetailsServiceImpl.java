package com.hanghae.onemanitnews.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hanghae.onemanitnews.common.exception.CommonException;
import com.hanghae.onemanitnews.common.exception.CommonExceptionEnum;
import com.hanghae.onemanitnews.entity.Member;
import com.hanghae.onemanitnews.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberDetailsServiceImpl {
    /*
     DB 내 계정정보 유무 검사
      - 사용자 정보를 DB에서 가져오고, UserDetailsImpl 생성자 매개값으로 전달
    */

	private final MemberRepository memberRepository;

	public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(email).orElseThrow(
			() -> new CommonException(CommonExceptionEnum.MEMBER_NOT_FOUND)
		);

		return new MemberDetailsImpl(member, member.getEmail(), member.getPassword());
	}
}
