package com.fitsync.config.oauth;

import com.fitsync.config.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
 * OAuth2 로그인 성공 시, 세션을 생성하는 대신 JWT를 생성하여
 * 프론트엔드에게 전달하는 역할을 하는 커스텀 성공 핸들러
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // '토큰 제작소'인 JwtTokenProvider를 주입받습니다.
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${cors.allowed-origins}")
    private String frontendURL;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * 로그인이 성공했을 때 자동으로 호출되는 메서드
     *
     * @param request        요청 정보
     * @param response       응답 정보
     * @param authentication Spring Security가 인증 과정에서 생성한 사용자 인증 정보
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. OAuth2 인증 성공 후, JWT 토큰 생성
        // - accessToken: API 요청 시 사용할 토큰 (짧은 유효기간)
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        // - refreshToken: accessToken 재발급용 토큰 (긴 유효기간)
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 2. refreshToken을 보안을 위해 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript로 접근 불가능하게 설정
        // 운영 환경에서만 Secure 쿠키 설정 (HTTPS에서만 전송)
        if ("prod".equals(activeProfile)) {
            refreshTokenCookie.setSecure(true);
        }
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일간 유효
        response.addCookie(refreshTokenCookie);

        // 3. 액세스 토큰은 URL 쿼리 파라미터로 전달
        String targetUrl = UriComponentsBuilder.fromUriString(frontendURL + "/auth/callback") // 프론트엔드에서 토큰을 처리할 경로
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        // 3. 클라이언트(브라우저)를 위에서 만든 targetUrl로 리디렉션시킵니다.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}