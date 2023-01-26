package com.hanghae.onemanitnews.common.security.dto;

import lombok.Getter;

/* 아직 쓰지 않음 추후에 사용 예정*/
@Getter
public enum MemberRoleEnum {
	ONEMAN(Authority.ONEMAN);  // 프로젝트 이용 가능한 사용자 권한

	//사용자 권한을 String 값으로 사용하기 위함
	private final String authority;

	MemberRoleEnum(String authority) {
		this.authority = authority;
	}

	public static class Authority {
		public static final String ONEMAN = "ROLE_ONEMAN";
	}
}
