package com.placementportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;

    /**
     * Access-token lifetime. Use Spring duration form: 30d, 14d, 12h (not raw milliseconds).
     * Example in properties: {@code jwt.expiration=30d} or env {@code JWT_EXPIRATION=30d}.
     */
    private Duration expiration = Duration.ofDays(30);

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Duration getExpiration() {
        return expiration;
    }

    public void setExpiration(Duration expiration) {
        this.expiration = expiration;
    }
}
