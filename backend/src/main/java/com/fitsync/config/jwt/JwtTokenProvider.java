package com.fitsync.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

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
        // 1. application.properties에서 받은 secretKey 문자열을 Base64 디코딩하여 byte 배열로 변환합니다.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // 2. 디코딩된 byte 배열을 사용하여 HMAC-SHA 암호화에 사용할 SecretKey 객체를 생성합니다.
        this.key = Keys.hmacShaKeyFor(keyBytes);
        // 3. 유효 기간을 초(seconds)에서 밀리초(milliseconds) 단위로 변환하여 저장합니다. (Java의 Date 객체는 밀리초를 사용)
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
        // 1. 인증된 사용자 정보에서 OAuth2User 객체를 가져옵니다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 2. OAuth2User 객체에서 사용자 이메일을 추출하여 토큰의 주체(subject)로 사용합니다.
        String email = (String) oAuth2User.getAttributes().get("email");
        // 3. 코드 재사용
        return createAccessToken(email);
    }

    /**
     * 리프레시 토큰을 생성하는 메서드입니다.
     * 액세스 토큰과 로직은 동일하지만, 유효 기간이 더 깁니다.
     *
     * @param authentication Spring Security에서 인증된 사용자 정보
     * @return 생성된 리프레시 토큰 문자열
     */
    public String createRefreshToken(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

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
    public String getEmail (String token) {
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
    

}