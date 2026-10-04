package com.placementportal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.email.from-address:}")
    private String senderEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${app.email.brevo-api-key:${BREVO_API_KEY:${EMAIL_PASS:${EMAIL_PASS_BREVO:}}}}")
    private String brevoApiKey;

    public void sendEmail(String toEmail, String subject, String text) {
        String user = fromEmail != null ? fromEmail.trim() : "";
        String pwd = mailPassword != null ? mailPassword.replaceAll("\\s+", "") : "";
        String apiKey = brevoApiKey != null ? brevoApiKey.replaceAll("\\s+", "") : "";

        String from = determineSender(user, senderEmail);

        // If a Brevo API key (xkeysib-...) is configured, use Brevo HTTP REST API (port 443)
        // This is essential for Render Free Tier which blocks outbound SMTP ports 25, 465, 587, 2525
        if (apiKey.startsWith("xkeysib-")) {
            log.info("Sending email via Brevo REST API (HTTPS port 443) to {}", toEmail);
            try {
                sendViaBrevoApi(apiKey, from, toEmail, subject, text);
                return;
            } catch (Exception e) {
                log.error("Brevo REST API sending failed to {}: {}", toEmail, e.getMessage(), e);
                throw new IllegalStateException("Brevo API error: " + e.getMessage(), e);
            }
        }

        if (!StringUtils.hasText(user)) {
            throw new IllegalStateException("SMTP username is not configured. Please set EMAIL_USER (or EMAIL_USER_BREVO) in Render environment variables.");
        }
        if (!StringUtils.hasText(pwd)) {
            throw new IllegalStateException("SMTP password / key is not configured. Please set EMAIL_PASS (or EMAIL_PASS_BREVO) in Render environment variables.");
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            if (StringUtils.hasText(from)) {
                mail.setFrom(from);
            }
            mail.setTo(toEmail);
            mail.setSubject(subject);
            mail.setText(text);
            log.info("Sending email via SMTP to {} (from: {})", toEmail, from);
            mailSender.send(mail);
            log.info("Email sent successfully to {}", toEmail);
        } catch (MailException e) {
            log.error("SMTP send failed to {} (from: {}): {}", toEmail, from, e.getMessage(), e);
            throw e;
        }
    }

    private void sendViaBrevoApi(String apiKey, String from, String toEmail, String subject, String text) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        Map<String, String> sender = new HashMap<>();
        sender.put("name", "PlacementBoard");
        sender.put("email", StringUtils.hasText(from) ? from : "thelazybeann@gmail.com");
        payload.put("sender", sender);

        List<Map<String, String>> toList = new ArrayList<>();
        Map<String, String> toRecipient = new HashMap<>();
        toRecipient.put("email", toEmail);
        toList.add(toRecipient);
        payload.put("to", toList);

        payload.put("subject", subject);
        payload.put("textContent", text);
        String html = "<div style=\"font-family: sans-serif; font-size: 15px; color: #222; line-height: 1.6;\">"
                + text.replace("\n", "<br/>") + "</div>";
        payload.put("htmlContent", html);

        String json = objectMapper.writeValueAsString(payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("api-key", apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            log.info("Brevo API email sent successfully to {} (status: {})", toEmail, resp.statusCode());
        } else {
            log.error("Brevo API error {}: {}", resp.statusCode(), resp.body());
            throw new IllegalStateException("HTTP " + resp.statusCode() + " - " + resp.body());
        }
    }

    private String determineSender(String user, String configuredSender) {
        if (StringUtils.hasText(configuredSender) && !"ajjgamerr@gmail.com".equalsIgnoreCase(configuredSender.trim())) {
            return configuredSender.trim();
        }
        if (StringUtils.hasText(user) && user.contains("@") && !user.endsWith("@smtp-brevo.com")) {
            return user;
        }
        if (StringUtils.hasText(configuredSender)) {
            return configuredSender.trim();
        }
        return user;
    }

    public void sendPasswordResetOtp(String toEmail, String otpCode) {
        String body = """
                Hi,

                Your PlacementPedia password reset code is: %s

                It expires in 10 minutes. If you did not request a reset, you can ignore this message.

                — PlacementBoard
                """.formatted(otpCode);
        sendEmail(toEmail, "Your PlacementBoard reset code", body.trim());
    }
}
