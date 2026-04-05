package com.placementportal.config;

import com.mongodb.ConnectionString;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Spring Data MongoDB rejects database names containing {@code . / " $} or spaces. Atlas or Render env
 * sometimes supplies a dotted name (e.g. {@code placement.portal}) or the default DB in the URI path contains
 * invalid characters. We normalize the effective name before {@code MongoDatabaseFactory} starts.
 */
public class SanitizeMongoDatabaseNameEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROP_DB = "spring.data.mongodb.database";
    private static final String PROP_URI = "spring.data.mongodb.uri";
    private static final String SOURCE_NAME = "mongoDatabaseNameSanitized";
    private static final String FALLBACK_DB = "placementportal";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String explicit = environment.getProperty(PROP_DB);
        String uri = environment.getProperty(PROP_URI);

        String candidate = explicit;
        if (isBlank(candidate) && uri != null && !uri.isBlank()) {
            String trimmed = uri.trim();
            if (trimmed.startsWith("mongodb://") || trimmed.startsWith("mongodb+srv://")) {
                try {
                    candidate = new ConnectionString(trimmed).getDatabase();
                } catch (Exception ignored) {
                    // leave candidate null
                }
            }
        }

        if (isBlank(candidate)) {
            return;
        }

        String sanitized = sanitizeDatabaseName(candidate);
        if (sanitized.equals(candidate)) {
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(PROP_DB, sanitized.isEmpty() ? FALLBACK_DB : sanitized);
        environment.getPropertySources().addFirst(new MapPropertySource(SOURCE_NAME, map));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Align with {@code MongoDatabaseFactorySupport} rules: no slashes, dots, spaces, quotes, or dollar signs.
     */
    static String sanitizeDatabaseName(String raw) {
        if (raw == null) {
            return FALLBACK_DB;
        }
        StringBuilder sb = new StringBuilder(raw.length());
        for (char c : raw.trim().toCharArray()) {
            if (c == '/' || c == '.' || c == ' ' || c == '"' || c == '$') {
                sb.append('_');
            } else {
                sb.append(c);
            }
        }
        String s = sb.toString();
        // avoid leading/trailing underscores from edge replacements
        while (s.startsWith("_")) {
            s = s.substring(1);
        }
        while (s.endsWith("_")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
