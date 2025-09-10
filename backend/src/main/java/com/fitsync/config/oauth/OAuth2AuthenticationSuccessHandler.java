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
        addCookie(response, "refreshToken", refreshToken, 60*60*24*7);

        // 로그인 성공 후, 더 이상 필요 없는 임시 인증 관련 쿠키를 정리
        deleteCookie(response, "oauth2_auth_request");

        // 3. 액세스 토큰은 URL 쿼리 파라미터로 프론트엔드에 전달
        String targetUrl = UriComponentsBuilder.fromUriString(frontendURL + "/auth/callback")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        // 4. 클라이언트를 프론트엔드의 콜백 URL로 리디렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * 지정된 이름과 값으로 쿠키를 생성하고 응답에 추가하는 핼퍼 메소드
     */
    private void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        String cookieValue;
        if ("prod".equals(activeProfile)) {
            cookieValue = String.format("%s=%s; Max-Age=%d; Path=/; Domain=fitsync.kro.kr; SameSite=None; Secure; HttpOnly", name, value, maxAge);
        } else {
            cookieValue = String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly", name, value, maxAge);
        }
        response.addHeader("Set-Cookie", cookieValue);
    }

    /**
     * 지정된 이름의 쿠키를 삭제하는 헬퍼 메소드
     */
    private void deleteCookie(HttpServletResponse response, String name) {
        addCookie(response, name, "", 0);
    }
}

