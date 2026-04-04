package com.placementportal.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Fails fast when required secrets / connection settings are missing (avoids obscure runtime errors).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StartupConfigurationValidator {

    private static final int MIN_JWT_SECRET_LENGTH = 32;

    private final Environment environment;

    @PostConstruct
    public void validate() {
        String mongoUri = environment.getProperty("spring.data.mongodb.uri");
        if (mongoUri == null || mongoUri.isBlank()) {
            throw new IllegalStateException(
                    "MongoDB URI is not set. Set MONGO_URI or spring.data.mongodb.uri in the environment.");
        }

        String jwtSecret = environment.getProperty("jwt.secret");
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException(
                    "JWT secret is not set. Set JWT_SECRET or jwt.secret (min " + MIN_JWT_SECRET_LENGTH + " chars for HS256).");
        }
        if (jwtSecret.length() < MIN_JWT_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "jwt.secret must be at least " + MIN_JWT_SECRET_LENGTH + " characters for HS256.");
        }
    }
}
