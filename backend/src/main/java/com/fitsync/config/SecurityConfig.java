package com.fitsync.config;

import com.fitsync.config.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화합니다.
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf 공격 방어 기능을 비활성화합니다. (토큰 방식에서는 사용하지 않음)
                .csrf(csrf -> csrf.disable())
                // 요청 경로에 대한 접근 권한을 설정합니다.
                .authorizeHttpRequests(auth -> auth
                        // "/" 경로는 모든 사용자에게 허용
                        .requestMatchers("/").permitAll()
                        // "/api/v1/**" 경로는 "USER" 역할을 가진 사용자에게만 허용
                        // .requestMatchers("/api/v1/**").hasRole("USER")
                        // 그 외의 모든 요청은 일단 허용 (나중에 API별로 세분화 가능)
                        .anyRequest().permitAll()
                )
                // OAuth2 로그인을 활성화하고 관련 설정을 시작합니다.
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 성공 후 사용자 정보를 가져올 때의 설정을 담당합니다.
                        .userInfoEndpoint(userInfo -> userInfo
                                // 이 부분이 핵심! 로그인 성공 시, 우리가 만든 CustomOAuth2UserService를 사용하도록 지정합니다.
                                .userService(customOAuth2UserService)
                        )
                );
        return http.build();
    }
}