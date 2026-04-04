package com.placementportal.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Browser CORS for REST + JWT. Set {@code CORS_ALLOWED_ORIGINS} (comma-separated) in production,
 * e.g. {@code https://your-frontend.onrender.com,https://www.yourdomain.com}.
 */
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * Comma-separated origin patterns (same rules as Spring's {@code allowedOriginPatterns}).
     * Example: {@code https://*.vercel.app,http://localhost:5173}
     */
    // Provide sensible defaults for local development and the deployed frontend/backends.
    private String allowedOriginPatterns =
            "https://placement-board-six.vercel.app," +
                    "https://placementboard.onrender.com," +
                    "https://*.vercel.app";

    public String getAllowedOriginPatterns() {
        return allowedOriginPatterns;
    }

    public void setAllowedOriginPatterns(String allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    public List<String> allowedOriginPatternsList() {
        if (allowedOriginPatterns == null || allowedOriginPatterns.isBlank()) {
            return List.of();
        }
        return Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
