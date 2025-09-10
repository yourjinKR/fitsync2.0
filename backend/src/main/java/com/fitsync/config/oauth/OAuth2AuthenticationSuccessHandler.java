package com.fitsync.config.oauth;

import com.fitsync.config.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시, JWT를 생성하여 프론트엔드에게 전달하는 커스텀 성공 핸들러
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${cors.allowed-origins}")
    private String frontendURL;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * 로그인이 성공했을 때 자동으로 호출되는 메서드
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 2. (수정) 환경에 따라 쿠키 속성을 동적으로 설정
        long maxAge = 60 * 60 * 24 * 7; // 7일 유효

        // 기본 쿠키 설정 (개발 환경용)
        String cookieValue = String.format(
                "refreshToken=%s; Max-Age=%d; Path=/; HttpOnly",
                refreshToken, maxAge
        );

        // 배포 환경(prod)일 경우에만 Domain, SameSite, Secure 속성을 추가합니다.
        if ("prod".equals(activeProfile)) {
            cookieValue = String.format(
                    "refreshToken=%s; Max-Age=%d; Path=/; Domain=fitsync.kro.kr; SameSite=None; Secure; HttpOnly",
                    refreshToken, maxAge
            );
        }

        // 생성된 쿠키 문자열을 응답 헤더에 추가
        response.addHeader("Set-Cookie", cookieValue);

        // 3. 액세스 토큰은 URL 쿼리 파라미터로 프론트엔드에 전달
        String targetUrl = UriComponentsBuilder.fromUriString(frontendURL + "/auth/callback")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        // 4. 클라이언트를 프론트엔드의 콜백 URL로 리디렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

