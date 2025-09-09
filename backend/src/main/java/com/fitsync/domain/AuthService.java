package com.fitsync.domain;


import com.fitsync.config.jwt.JwtTokenProvider;
import com.fitsync.domain.user.User;
import com.fitsync.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * 토큰 재발급 및 로그아웃과 같은 인증 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * 리프레시 토큰을 받아 유효성을 검증하고, 새로운 액세스 토큰을 발급합니다.
     * @param refreshToken HttpOnly 쿠키로 전달받은 리프레시 토큰
     * @return 새로 발급된 액세스 토큰
     */
    public String refreshAccessToken(String refreshToken) {
        // 1. 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않는 리프레시 토큰입니다");
        }

        // 2. 리프레시 토큰에서 사용자 이메일을 추출
        String email = jwtTokenProvider.getEmail(refreshToken);

        // 3. 이메일을 이용하여 DB에서 사용자를 찾음
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다" + email));

        // 4. 해당 사용자에 대한 새로운 Authentication 객체를 생성합니다
        Authentication authentication = new UsernamePasswordAuthenticationToken(
          user.getEmail(),
          "",
          Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        return jwtTokenProvider.createAccessToken(authentication);

    }
}