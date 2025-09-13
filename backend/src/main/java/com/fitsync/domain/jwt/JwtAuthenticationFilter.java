package com.fitsync.domain.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * API 요청 헤더에 담긴 JWT 토큰을 검증하고, 유효하다면 SecurityContext에 인증 정보를 저장하는 필터입니다.
 * 모든 요청에 대해 한 번만 실행됩니다.
 */
@Component // (중요) 이 필터를 Spring Bean으로 등록해야 SecurityConfig에서 주입받을 수 있습니다.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter를 통해 한번만 실행함

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT 토큰을 추출합니다 (Authorization: Bearer xxx 형식)
        String token = resolveToken(request);

        // 2. 토큰 검증 및 인증 처리
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 2-1. 유효한 토큰에서 Authentication 객체 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // 2-2. SecurityContext에 Authentication 객체를 저장
            // Spring Security는 이 컨텍스트를 통해 현재 요청이 인증되었는지 확인
            // @AuthenticationPrincipal 등의 어노테이션으로 컨트롤러에서 사용 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }

    /**
     * HttpServletRequest의 헤더에서 'Authorization' 토큰을 추출합니다.
     *
     * @param request 현재 HTTP 요청
     * @return "Bearer " 접두사를 제거한 순수 토큰 문자열, 또는 토큰이 없으면 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
