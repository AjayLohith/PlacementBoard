package com.placementportal.config;

import com.mongodb.MongoClientSettings;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB tuning for Atlas / cloud: timeouts and pool bounds. URI and credentials stay in
 * {@code spring.data.mongodb.uri} / {@code MONGO_URI} only — never hardcoded here.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsCustomizer() {
        return (MongoClientSettings.Builder builder) -> builder
                .applyToClusterSettings(cluster ->
                        cluster.serverSelectionTimeout(30, TimeUnit.SECONDS))
                .applyToConnectionPoolSettings(pool -> pool
                        .maxSize(50)
                        .minSize(2)
                        .maxWaitTime(2, TimeUnit.MINUTES));
    }
}
