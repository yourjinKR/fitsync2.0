package com.fitsync.config;

import com.fitsync.domain.jwt.JwtAuthenticationFilter;
import com.fitsync.domain.oauth.CustomOAuth2UserService;
import com.fitsync.domain.oauth.OAuth2AuthenticationFailureHandler;
import com.fitsync.domain.oauth.OAuth2AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // 스프링의 설정 클래스임을 나타냅니다
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    // OAuth2 인증 처리를 위한 커스텀 서비스
    private final CustomOAuth2UserService customOAuth2UserService;
    // OAuth2 인증 성공 시 JWT 토큰 생성 등을 처리하는 핸들러
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    // 실패 핸들러 추가
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    // JWT 토큰 검증 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 인증 없이 접근 가능한 URL 패턴들
    private static final String[] PERMIT_ALL_PATTERNS = {
            "/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/error",
            "/oauth2/**", "/api/auth/**", "/health" // OAuth2 관련 경로와 토큰 재발급 등의 인증 관련 경로
    };

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://d25pkmq22vlln0.cloudfront.net"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 보호 비활성화 (JWT를 사용하므로 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                // 기본 http 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 폼 로그인 비활성화 (OAuth2 사용)
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션 비활성화 (JWT 사용으로 인해 세션이 필요없음)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // (핵심 추가) API 요청에 대한 예외 처리 구성
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 인증되지 않은 사용자가 보호된 API 리소스에 접근할 때
                            // 로그인 페이지로 리다이렉트하는 대신 401 Unauthorized 에러를 응답합니다.
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                )

                // URL 기반의 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // PERMIT_ALL_PATTERNS에 정의된 경로는 인증 없이 접근 가능
                        .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
                        // /api/** 경로는 인증된 사용자만 접근 가능
                        .requestMatchers("/api/**").authenticated()
                        // 그 외 모든 요청도 인증 필요
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // OAuth2 로그인 성공 시 사용자 정보를 가져올 때 사용할 서비스 설정
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        // OAuth2 로그인 성공 시 실행될 핸들러 설정
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        // 실패시 핸들러 설정
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                // JWT 검증 필터를 Spring Security 필터 체인에 추가
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

