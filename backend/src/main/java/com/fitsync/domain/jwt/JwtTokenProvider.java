package com.fitsync.domain.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JWT(JSON Web Token)를 생성하고 검증하는 역할을 담당하는 클래스입니다.
 * '토큰 제작소'와 같은 역할을 하며, 보안과 관련된 핵심 로직을 처리합니다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * JWT를 서명(sign)하고 검증하는 데 사용될 암호화 키입니다.
     * 이 키는 외부에 노출되어서는 안 됩니다.
     */
    private final SecretKey key;

    /**
     * 액세스 토큰(Access Token)의 유효 기간(초 단위)입니다.
     * 이 시간 동안만 API 요청이 가능합니다.
     */
    private final long accessTokenValidityInSeconds;

    /**
     * 리프레시 토큰(Refresh Token)의 유효 기간(초 단위)입니다.
     * 액세스 토큰이 만료되었을 때, 새로운 액세스 토큰을 발급받기 위해 사용됩니다.
     */
    private final long refreshTokenValidityInSeconds;

    /**
     * 생성자입니다.
     * application.properties 파일에 정의된 JWT 관련 설정값들을 주입받아 초기화합니다.
     *
     * @param secretKey                     JWT 서명에 사용할 비밀 키 (Base64 인코딩된 문자열)
     * @param accessTokenValidityInSeconds  액세스 토큰의 유효 기간(초)
     * @param refreshTokenValidityInSeconds 리프레시 토큰의 유효 기간(초)
     */
    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds * 1000;
    }


    /**
     * (추가) 이메일 문자열을 직접 받아 액세스 토큰을 생성하는 메서드입니다.
     * 토큰 재발급 시 사용됩니다.
     *
     * @param email 사용자의 이메일
     * @return 생성된 액세스 토큰 문자열
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInSeconds);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /**
     * 액세스 토큰을 생성하는 메서드입니다.
     *
     * @param authentication Spring Security에서 인증된 사용자 정보
     * @return 생성된 액세스 토큰 문자열
     */
    public String createAccessToken(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 카카오의 경우 이메일이 kakao_account 안에 있음
        String email;
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        } else {
            email = (String) attributes.get("email");
        }
        
        if (email == null) {
            throw new IllegalArgumentException("이메일을 찾을 수 없습니다: " + attributes);
        }
        
        return createAccessToken(email);
    }

    /**
     * 리프레시 토큰을 생성하는 메서드입니다.
     * 액세스 토큰과 로직은 동일하지만, 유효 기간이 더 깁니다.
     *
     * @param authentication Spring Security에서 인증된 사용자 정보
     * @return 생성된 리프레시 토큰 문자열
     */
    @SuppressWarnings("unchecked")
    public String createRefreshToken(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 카카오의 경우 이메일이 kakao_account 안에 있음
        String email;
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        } else {
            email = (String) attributes.get("email");
        }
        
        if (email == null) {
            throw new IllegalArgumentException("이메일을 찾을 수 없습니다: " + attributes);
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + this.refreshTokenValidityInSeconds);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // 토큰에서 사용자 이메일 추출
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다");
        }
        return false;
    }

    /**
     * (추가) JWT 토큰에서 인증 정보를 조회하는 메서드입니다.
     * 이 메서드는 JwtAuthenticationFilter에서 사용됩니다.
     *
     * @param token 유효한 JWT 토큰
     * @return Spring Security가 이해할 수 있는 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        String email = getEmail(token);
        // 실제로는 DB에서 User를 조회하여 권한 정보를 가져와야 하지만,
        // 여기서는 예시로 "ROLE_USER" 권한을 부여합니다.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        // email을 principal로, 비밀번호는 비워두고, 권한 목록을 담아 Authentication 객체를 생성합니다.
        return new UsernamePasswordAuthenticationToken(email, "", authorities);
    }
}

