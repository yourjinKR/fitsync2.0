package com.fitsync.domain.auth;


import com.fitsync.domain.jwt.JwtTokenProvider;
import com.fitsync.domain.user.entity.User;
import com.fitsync.domain.user.entity.UserType;
import com.fitsync.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
     *
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

        UserType userType = user.getType();
        List<String> roles = List.of(userType.name());

        // 4. (수정) 불필요한 Authentication 객체 생성 로직을 제거했습니다.
        // DB에서 조회한 사용자의 이메일을 직접 사용하여 액세스 토큰을 생성합니다.
        // 이렇게 하면 String을 OAuth2User로 변환하려는 문제가 발생하지 않습니다.
        return jwtTokenProvider.createAccessToken(user.getEmail(), roles);

    }
}
