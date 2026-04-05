package com.placementportal.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final MongoTemplate mongoTemplate;

    /**
     * Liveness: no Mongo call — fast 200 for uptime monitors (avoids false downs on slow ping/timeouts).
     * Point UptimeRobot / Render health checks here.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    /** Readiness: verifies MongoDB connectivity (optional deeper check). */
    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, String>> ready() {
        mongoTemplate.getDb().runCommand(new Document("ping", 1));
        return ResponseEntity.ok(Map.of("status", "UP", "mongo", "UP"));
    }
}
