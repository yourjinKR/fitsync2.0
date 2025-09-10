package com.fitsync.config.oauth;

import com.fitsync.config.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * OAuth2 로그인 성공 시, RefreshToken은 HttpOnly 쿠키에 담고,
 * AccessToken은 부모 창으로 전달하는 스크립트가 포함된 HTML을 응답하는 최종 핸들러.
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 1. AccessToken과 RefreshToken을 모두 생성합니다.
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 2. RefreshToken을 HttpOnly 쿠키에 안전하게 저장합니다.
        addRefreshTokenToCookie(response, refreshToken);

        // 3. 리다이렉트 대신, 토큰을 전달하고 스스로 닫히는 HTML 페이지를 응답합니다.
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>로그인 처리 중...</title></head>");
        out.println("<body>");
        out.println("<script>");
        // 부모 창(React 앱)으로 accessToken을 메시지로 보냅니다.
        out.println("window.opener.postMessage({ type: 'loginSuccess', accessToken: '" + accessToken + "' }, '*');");
        // 메시지를 보낸 후 현재 팝업 창을 닫습니다.
        out.println("window.close();");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenValidityInSeconds)
                .secure("prod".equals(activeProfile))
                .sameSite("prod".equals(activeProfile) ? "None" : "Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}

