package com.hanghae.onemanitnews.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;

@Getter
@Entity
public class Member extends Timestamped {

	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long memberId;

	@Column(nullable = false, unique = true, length = 50)
	private String email;

	@Column(nullable = false) //length = 255
	private String password;

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	private List<Subscribe> subscribe = new ArrayList<>();
}
