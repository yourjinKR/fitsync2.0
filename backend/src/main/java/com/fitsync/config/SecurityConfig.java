package com.fitsync.config;

import com.fitsync.config.oauth.CustomOAuth2UserService;
import com.fitsync.config.oauth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // 로그인 없이 접근 가능한 경로들
    private static final String[] PERMIT_ALL_PATTERNS = {
            "/",                // 홈 화면
            "/css/**",          // 정적 리소스 (CSS)
            "/images/**",       // 정적 리소스 (이미지)
            "/js/**",           // 정적 리소스 (JavaScript)
            "/favicon.ico",
            "/error",
            "/oauth2/**",       // 소셜 로그인 관련 경로
            "/api/auth/**"      // 토큰 재발급, 로그아웃 등 인증 관련 API
    };

    // cors 설정을 위한 Bean을 추가합니다
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://d25pkmq22vlln0.cloudfront.net"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 쿠키를 포함한 요청을 허용하기 위한 중요한 설정
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    // 메서드 파라미터에서 핸들러를 제거
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // .csrf(csrf -> csrf.disable()) 보다 더 권장되는 문법 (람다식 대신 메소드 참조 사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // 필드로 주입받은 핸들러를 사용
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                );
        return http.build();
    }
}