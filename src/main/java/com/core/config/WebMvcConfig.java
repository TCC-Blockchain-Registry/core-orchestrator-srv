package com.core.config;

import com.core.adapter.input.interceptors.RequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Registers interceptors and other web configurations
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestInterceptor requestInterceptor;

    public WebMvcConfig(RequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/api/**") // Apply to all /api/** routes
                .excludePathPatterns(
                    // Explicitly exclude public endpoints (double safety)
                    "/api/users/login",
                    "/api/users/register",
                    "/api/health",
                    "/api/webhook/**",
                    "/api/mock/**"
                );
    }
}

