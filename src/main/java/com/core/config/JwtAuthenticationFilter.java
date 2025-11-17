package com.core.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * Extracts userId from JWT token and adds it as a request attribute
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                Long userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);
                String role = jwtService.extractRole(token);
                
                request.setAttribute("userId", userId);
                request.setAttribute("userEmail", email);
                request.setAttribute("userRole", role);
                request.setAttribute("authenticated", true);
            } catch (Exception e) {
                // Token inválido - não adiciona atributos
                // A rota protegida vai retornar 401
                request.setAttribute("authenticated", false);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}

