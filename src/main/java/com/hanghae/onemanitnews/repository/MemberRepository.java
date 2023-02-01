package com.hanghae.onemanitnews.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanghae.onemanitnews.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	@Query(value = "INSERT INTO member (member_id, email, password) "
		+ "VALUES((SELECT LAST_INSERT_ID() + 1), :email, :password)",
		nativeQuery = true)
	@Modifying
	void last_increment_id(@Param("email") String email, @Param("password") String encryptPassword);
}
