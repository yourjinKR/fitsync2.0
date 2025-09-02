package com.fitsync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 이 클래스가 Spring의 설정 파일임을 나타냅니다.
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // '/api/'로 시작하는 모든 경로에 대해 CORS 설정을 적용합니다.
                .allowedOrigins("http://localhost:3000") // 'http://localhost:3000'からのリクエストを許可します。
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드를 지정합니다.
                .allowedHeaders("*") // 모든 HTTP 헤더를 허용합니다.
                .allowCredentials(true); // 쿠키와 같은 인증 정보를 허용합니다.
    }
}