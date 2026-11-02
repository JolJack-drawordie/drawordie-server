package com.pentagon.drawordie.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// 요청마다 "Authorization: Bearer <토큰>" 헤더를 확인해, 토큰이 유효하면 그 유저로 로그인된 상태로 만든다
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            Long userId = jwtTokenProvider.getUserId(header.substring(BEARER_PREFIX.length()));
            if (userId != null) {
                // 유저 번호를 principal로 → 컨트롤러에서 @AuthenticationPrincipal Long userId로 받는다
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 토큰이 없거나 틀려도 그대로 넘기고, 로그인이 필요한 API면 SecurityConfig에서 401
        filterChain.doFilter(request, response);
    }
}
