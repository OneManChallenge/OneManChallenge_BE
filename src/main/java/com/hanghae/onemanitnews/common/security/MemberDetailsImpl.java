package com.hanghae.onemanitnews.common.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hanghae.onemanitnews.common.security.dto.MemberRoleEnum;
import com.hanghae.onemanitnews.entity.Member;

public class MemberDetailsImpl implements UserDetails {
	private final Member member; //인증 완료된 Member 객체
	private final String email; //인증 완료된 Member email
	private final String password; //인증 완료된 Member email

	//생성자
	public MemberDetailsImpl(Member member, String email, String password) {
		this.member = member;
		this.email = email;
		this.password = password;
	}

	//게터
	public Member getMember() {
		return member;
	}

	// Member의 권한을 가져오고, Collection에 넣어서 반환함.
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//MemberRoleEnum role = member.getRole();
		MemberRoleEnum role = MemberRoleEnum.ONEMAN;  //프로젝트 회원만 회원으로 인정
		String authority = role.getAuthority();

		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);

		return authorities;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

}
