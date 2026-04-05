package com.placementportal.service;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.placementportal.config.AiProperties;
import com.placementportal.dto.ArticleAiFillResponse;
import com.placementportal.dto.JobPostingAiFillResponse;
import com.placementportal.exception.ValidationException;
import com.placementportal.util.JobAudienceTagResolver;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAiParseService {

    /** Parses LLM "JSON" where models sometimes put raw newlines inside quoted strings (invalid strict JSON). */
    private static final ObjectMapper AI_JSON = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .build();

    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s<>\"']+");

    private static final String JOB_SYSTEM = """
            You extract structured job posting data from messy pasted text (emails, WhatsApp, PDF paste, links).
            Return a single JSON object with EXACTLY these keys (use null or empty string only if unknown):
            title — short, precise job title only.
            companyName — employer name if inferable.
            description — role summary: responsibilities, compensation hints, work mode; do NOT repeat the full skills list here (keep skills in skillsRequired).
            applyLink — one http(s) URL if any appears in the text; else null.
            location — city/region or Remote/Hybrid.
            jobType — one of: intern, full-time, part-time, contract (lowercase).
            skillsRequired — required skills, tools, languages, frameworks as a clear bullet-style or comma-separated block.
            passoutYear — graduation / passout batches for eligibility (e.g. "2025", "2024 and 2025", "2026 only").
            qualificationMajor — degree or major if stated (e.g. "B.Tech", "B.E. Computer Science").
            qualificationBranch — branch or stream (e.g. "CSE", "IT", "ECE") if stated.
            qualificationYear — graduating batch year(s) for eligibility (e.g. "2025", "2024–2026").
            experienceText — if the role is for fresh graduates, use exactly "Fresher"; otherwise a short line like "3 years backend Java" or "2+ years".
            audienceTag — either "FRESHERS" or "EXPERIENCED": use FRESHERS for campus / new grad / 2024–2026 batch roles; EXPERIENCED when prior years of industry experience are required.
            postedOn — when posted or application deadline as short text (e.g. "Deadline: 15 Feb 2026" or "Posted January 2026").
            Output must be valid JSON: inside string values use \\n for line breaks in multiline fields (never raw newlines inside quotes). \
            JSON only. No markdown fences, no commentary.""";

    private static final String ARTICLE_SYSTEM = """
            You edit for a student-facing tech and career-skills publication. The user pastes rough, unstructured \
            input (bullets, fragments, links, half-formed ideas). Your job:
            1) Identify their core intent and thesis — stay faithful; sharpen wording; do not pivot to unrelated topics.
            2) Produce publication-quality prose: clear, confident, on-point. Expand thin notes with useful context \
            students need, without inventing false facts, companies, or credentials.
            3) body must read as flowing article text: logical paragraphs only. Do NOT add standalone lines that act as \
            generic section labels (for example avoid: Introduction, Why it matters, What next, Conclusion, Summary, \
            Key takeaways as headers). The reader should still get setup, depth, and practical closure through natural \
            prose — no template headings; use paragraph breaks (blank lines) between ideas. No markdown code fences \
            (no triple backticks). For tools, products, and cloud platforms (e.g. Tableau, Power BI, Excel, AWS, Azure, \
            Google Cloud), write them as normal words — no backticks. Reserve single backticks only for real code, \
            commands, file paths, or API identifiers when truly needed.
            4) title: specific, compelling, not clickbait.
            5) slug: lowercase kebab-case, ASCII, no spaces.
            6) excerpt: at most two sentences; hook the reader; plain text.
            7) published: false unless the user explicitly asks to publish or go live now.
            Return one JSON object with keys: title, slug, excerpt, body, published. The object must be valid JSON: \
            inside string values use \\n for line breaks (never raw newlines inside quotes). JSON only. No markdown wrapper.""";

    private final ObjectMapper objectMapper;
    private final AiProperties aiProperties;

    public JobPostingAiFillResponse parseJobPosting(String rawText) {
        requireConfigured();
        String trimmed = rawText.trim();
        if (trimmed.length() > 24_000) {
            throw new ValidationException("Text is too long. Paste under ~24k characters.");
        }
        String content = callGroq(JOB_SYSTEM, trimmed, 0.2);
        try {
            JsonNode n = AI_JSON.readTree(stripJsonFence(content));
            String apply = text(n, "applyLink");
            if (apply == null || apply.isBlank()) {
                apply = firstUrlInText(trimmed);
            }
            JobPostingAiFillResponse parsed = JobPostingAiFillResponse.builder()
                    .title(text(n, "title"))
                    .companyName(text(n, "companyName"))
                    .description(text(n, "description"))
                    .applyLink(apply)
                    .location(text(n, "location"))
                    .jobType(normalizeJobType(text(n, "jobType")))
                    .skillsRequired(text(n, "skillsRequired"))
                    .passoutYear(text(n, "passoutYear"))
                    .qualificationMajor(text(n, "qualificationMajor"))
                    .qualificationBranch(text(n, "qualificationBranch"))
                    .qualificationYear(text(n, "qualificationYear"))
                    .experienceText(text(n, "experienceText"))
                    .audienceTag(normalizeAiAudienceTag(text(n, "audienceTag")))
                    .postedOn(text(n, "postedOn"))
                    .build();
            String resolvedTag = JobAudienceTagResolver.resolve(
                    parsed.getAudienceTag(),
                    parsed.getExperienceText(),
                    parsed.getPassoutYear(),
                    parsed.getQualificationYear());
            return parsed.toBuilder().audienceTag(resolvedTag).build();
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to map job AI JSON: {}", e.getMessage());
            throw new ValidationException("AI returned invalid data. Try again or shorten the paste.");
        }
    }

    public ArticleAiFillResponse parseArticle(String rawText) {
        requireConfigured();
        String trimmed = rawText.trim();
        if (trimmed.length() > 24_000) {
            throw new ValidationException("Text is too long. Paste under ~24k characters.");
        }
        String content = callGroq(ARTICLE_SYSTEM, trimmed, 0.45);
        try {
            JsonNode n = AI_JSON.readTree(stripJsonFence(content));
            boolean pub = false;
            if (n.has("published") && !n.get("published").isNull()) {
                pub = n.get("published").asBoolean(false);
            }
            return ArticleAiFillResponse.builder()
                    .title(text(n, "title"))
                    .slug(text(n, "slug"))
                    .excerpt(text(n, "excerpt"))
                    .body(text(n, "body"))
                    .published(pub)
                    .build();
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to map article AI JSON: {}", e.getMessage());
            throw new ValidationException("AI returned invalid data. Try again or shorten the paste.");
        }
    }

    private void requireConfigured() {
        if (aiProperties.getApiKey() == null || aiProperties.getApiKey().isBlank()) {
            throw new ValidationException("AI is not configured. Set AI_API_KEY (e.g. in server .env) or app.ai.api-key.");
        }
    }

    private String callGroq(String systemPrompt, String userText, double temperature) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", aiProperties.getModel().trim());
        body.put("temperature", temperature);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userText)));

        String base = aiProperties.getBaseUrl().replaceAll("/$", "");
        String url = base + "/chat/completions";

        try {
            String requestJson = objectMapper.writeValueAsString(body);
            String responseJson = RestClient.create()
                    .post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.getApiKey().trim())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(requestJson)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                throw new ValidationException("Unexpected AI response shape.");
            }
            String out = choices.get(0).path("message").path("content").asText("");
            if (out.isBlank()) {
                throw new ValidationException("Empty AI response.");
            }
            return out.trim();
        } catch (RestClientResponseException e) {
            String err = e.getResponseBodyAsString();
            log.error("Groq HTTP {}: {}", e.getStatusCode(), err);
            throw new ValidationException("AI request failed (" + e.getStatusCode().value() + "). Check API key and model name.");
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Groq call failed", e);
            throw new ValidationException("Could not reach AI service: " + e.getMessage());
        }
    }

    private static String stripJsonFence(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        if (s.startsWith("```")) {
            int nl = s.indexOf('\n');
            if (nl > 0) {
                s = s.substring(nl + 1);
            } else {
                s = s.substring(3);
            }
            int end = s.lastIndexOf("```");
            if (end >= 0) {
                s = s.substring(0, end);
            }
        }
        return s.trim();
    }

    private static String text(JsonNode n, String field) {
        if (n == null || !n.has(field) || n.get(field).isNull()) {
            return null;
        }
        String s = n.get(field).asText("").trim();
        return s.isEmpty() ? null : s;
    }

    private static String normalizeAiAudienceTag(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("FRESHER")) {
            return JobAudienceTagResolver.FRESHERS;
        }
        if (u.contains("EXPERIENC")) {
            return JobAudienceTagResolver.EXPERIENCED;
        }
        return JobAudienceTagResolver.normalizeExplicit(raw);
    }

    private static String normalizeJobType(String t) {
        if (t == null) {
            return null;
        }
        String x = t.toLowerCase().replace(' ', '-');
        if (x.contains("intern")) {
            return "intern";
        }
        if (x.contains("full")) {
            return "full-time";
        }
        if (x.contains("part")) {
            return "part-time";
        }
        if (x.contains("contract")) {
            return "contract";
        }
        return t;
    }

    private static String firstUrlInText(String text) {
        Matcher m = URL_PATTERN.matcher(text);
        return m.find() ? m.group().replaceAll("[),.;]+$", "") : null;
    }
}
