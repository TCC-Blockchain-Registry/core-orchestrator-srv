package com.core.adapter.input.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Request Interceptor
 * Blocks requests without valid JWT token except for public endpoints
 */
@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        
        String requestURI = request.getRequestURI();
        
        // 🔓 PUBLIC ENDPOINTS - Allow without authentication
        if (isPublicEndpoint(requestURI)) {
            return true;
        }
        
        // 🔒 PROTECTED ENDPOINTS - Require authentication
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        
        if (authenticated == null || !authenticated) {
            // Return 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"Unauthorized\",\"message\":\"Valid JWT token required\"}"
            );
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if the endpoint is public (doesn't require authentication)
     */
    private boolean isPublicEndpoint(String uri) {
        // Login and Registration
        if (uri.equals("/api/users/login") || uri.equals("/api/users/register")) {
            return true;
        }
        
        // Health check endpoints
        if (uri.equals("/api/health") || uri.startsWith("/actuator/")) {
            return true;
        }
        
        // Swagger/OpenAPI documentation
        if (uri.startsWith("/swagger-ui") || 
            uri.startsWith("/v3/api-docs") || 
            uri.equals("/swagger-ui.html")) {
            return true;
        }
        
        // Webhook endpoints (should be protected by other means, like API keys)
        if (uri.startsWith("/api/webhook/")) {
            return true;
        }
        
        // Mock endpoints (only for development)
        if (uri.startsWith("/api/mock/")) {
            return true;
        }
        
        return false;
    }
}

