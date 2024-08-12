package com.example.taskmanagment.config;

import com.example.taskmanagment.handler.customSecurityHandlers.CustomAccessDeniedHandler;
import com.example.taskmanagment.services.JwTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwTokenService tokenService;

    private final CustomAccessDeniedHandler exceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            SecurityContext context = SecurityContextHolder.getContext();
            try {
                String email = tokenService.getEmail(token);
                if (email != null && context.getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            null
                    );
                    context.setAuthentication(authentication);
                }
            } catch (JwtException e) {

                exceptionHandler.handle(request, response, new AccessDeniedException(e.getMessage()));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}