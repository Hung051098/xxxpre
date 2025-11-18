package com.vn.hung.xxxpre.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply CORS to all paths starting with /api/
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow all standard methods, including OPTIONS
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true)
                .maxAge(3600); // Cache pre-flight response for 1 hour
    }
}
