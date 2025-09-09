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

    /**
     * 로그인이 성공했을 때 자동으로 호출되는 메서드
     *
     * @param request        요청 정보
     * @param response       응답 정보
     * @param authentication Spring Security가 인증 과정에서 생성한 사용자 인증 정보
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. JwtTokenProvider를 사용해 액세스 토큰과 리프레시 토큰을 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 2. 프론트엔드로 리디렉션할 URL을 만든 후 URL의 쿼리 파라미터에 생성된 토큰들을 담아 전달합니다.
        String targetUrl = UriComponentsBuilder.fromUriString(frontendURL + "/auth/callback") // 프론트엔드에서 토큰을 처리할 경로
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        // 3. 클라이언트(브라우저)를 위에서 만든 targetUrl로 리디렉션시킵니다.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}