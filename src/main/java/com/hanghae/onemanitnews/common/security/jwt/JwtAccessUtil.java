package com.hanghae.onemanitnews.common.security.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hanghae.onemanitnews.common.security.MemberDetailsServiceImpl;
import com.hanghae.onemanitnews.common.security.dto.MemberRoleEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component  //개발자가 작성한 class를 Bean으로 등록
@PropertySource("classpath:application-security.properties")
@RequiredArgsConstructor
public class JwtAccessUtil {

	//스프링시큐리티 실습
	private final MemberDetailsServiceImpl memberDetailsService;

	// 인증 객체 생성
	public Authentication createAuthentication(String email) {
		UserDetails userDetails = memberDetailsService.loadUserByEmail(email);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	//1. 토큰에 필요한 값
	public static final String ACCESS_HEADER = "Authorization";  // Header KEY 값(위에 스샷 참고.. 헤더 키값)
	public static final String AUTHORIZATION_KEY = "accessAuth";  // 사용자 권한 값의 KEY 값 – token안에 사용자 권한 식별용
	private static final String ONEMAN_PREFIX = "Oneman ";  // Token 식별자 – 토큰 앞에 붙임
	private static final long TOKEN_TIME = 10 * 60 * 1000L;  // 토큰 만료시간(10분)

	@Value("${jwt.secret.key.access}") //시크릿키 값 가져옴, auth.properties 참고
	private String secretKeyAccess;
	private Key accessKey;  //토큰을 만들 때 넣어줄 킷값, 아래 init() 참고
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {  // 키 객체 값 만드는 부분
		byte[] bytes = Base64.getDecoder().decode(secretKeyAccess);
		accessKey = Keys.hmacShaKeyFor(bytes);
	}
	// @PostConstruct 처음 객체 생성 시 초기화하는 어노테이션,
	// 의존성 주입이 이루어진 후 초기화 수행, 근데 아래보니까 HMAC 쓰는데, 암호화할 키 값 만들 때 쓰는 듯,
	// HMAC(키 XOR 메시지 => 해시함수 => 키 XOR => 해시함수)

	//2. header 토큰을 가져오기
	public String resolveAccessToken(HttpServletRequest request) { //HttpServletRequest 객체 안에 토큰이 헤더값으로 들어있음
		String bearerToken = request.getHeader(ACCESS_HEADER); //토큰값 가져오기
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(ONEMAN_PREFIX)) { //앞에 토큰 식별자 있는지 체크 Oenman
			return bearerToken.substring(7); //디코딩한 후에는 필요없으니 Bearer 문자열은 제외
		}
		return null;
	}

	//3. 토큰 생성
	public String createAccessToken(String email, MemberRoleEnum role) {
		Date date = new Date();

		//아래 부분들에 각각 값 세팅하는 부분
		return ONEMAN_PREFIX +  //Oneman 문자열 들어가는 부분
			Jwts.builder()  //jwt 만들기ㄱㄱ
				.setSubject(email)  //유저명 넣고
				.claim(AUTHORIZATION_KEY, role)  //claim에는 사용자권한 값 넣고
				.setExpiration(new Date(date.getTime() + TOKEN_TIME)) //여긴 토큰 유효기간 넣고, 1시간
				.setIssuedAt(date)  //토큰이 언제 생성되었는지
				.signWith(accessKey, signatureAlgorithm)  //사용할 암호알고리즘 세팅
				.compact();   //압축하고 암호화 ㄱㄱ
	}

	//4. 토큰 검증
	public String validateAccessToken(String accessToken) {
		try {
			//토큰값과 암호화 킷값 사용
			Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken);
			return "Access";
		} catch (SecurityException | MalformedJwtException e) {
			//유효하지 않은 토큰 예외 발생
			log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			//유효하지 않은 토큰 예외 발생
			log.info("Expired JWT token, 만료된 JWT token 입니다.");
			return "Refresh";
		} catch (UnsupportedJwtException e) {
			//유효하지 않은 토큰 예외 발생
			log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (SignatureException e) {
			log.info("SignatureException, 서명 예외 발생");
		} catch (IllegalArgumentException e) {
			//유효하지 않은 토큰 예외 발생
			log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return "NO";
	}

	//5. 토큰에서 사용자 정보 가져오기, getBody()를 통해 사용자 정보 가져옴
	public Claims getMemberInfoFromAccessToken(String accessToken) {
		return Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken).getBody();
	}
}