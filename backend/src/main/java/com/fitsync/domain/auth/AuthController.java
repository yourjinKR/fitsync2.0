package com.fitsync.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // (추가) 현재 활성화된 프로필을 주입받습니다.
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new AccessTokenResponse(newAccessToken));
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // (수정) 환경에 따라 쿠키를 삭제하는 로직
        String cookieValue;

        // 배포 환경(prod)일 경우, 반드시 Domain 속성을 포함하여 쿠키를 삭제해야 합니다.
        if ("prod".equals(activeProfile)) {
            // (수정) 불필요한 String.format 제거
            cookieValue = "refreshToken=; Max-Age=0; Path=/; Domain=.fitsync.kro.kr; SameSite=None; Secure; HttpOnly";
        } else {
            // 개발 환경에서는 Domain 속성 없이 쿠키를 삭제합니다.
            // (수정) 불필요한 String.format 제거
            cookieValue = "refreshToken=; Max-Age=0; Path=/; HttpOnly";
        }

        response.addHeader("Set-Cookie", cookieValue);

        return ResponseEntity.ok().build();
    }

    public record AccessTokenResponse(String accessToken) {
    }
}

