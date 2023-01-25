package com.hanghae.onemanitnews.mapper;

import org.springframework.stereotype.Component;

import com.hanghae.onemanitnews.entity.Member;

@Component
public class MemberMapper {
	public Member toEntity(String email, String encryptPassword) {
		return Member.builder()
			.email(email)
			.password(encryptPassword)
			.build();
	}
}
