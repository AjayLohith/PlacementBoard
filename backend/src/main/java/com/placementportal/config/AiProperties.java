package com.placementportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Groq uses an OpenAI-compatible HTTP API ({@code /v1/chat/completions}).
 * Set {@code AI_API_KEY} (e.g. from {@code .env}) or {@code app.ai.api-key}. Not exposed to the browser.
 */
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    private String apiKey = "";

    private String baseUrl = "https://api.groq.com/openai/v1";

    /** e.g. llama-3.3-70b-versatile, llama-3.1-8b-instant, mixtral-8x7b-32768 */
    private String model = "llama-3.3-70b-versatile";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
