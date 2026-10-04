package com.placementportal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.email.from-address:}")
    private String senderEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public void sendEmail(String toEmail, String subject, String text) {
        String user = fromEmail != null ? fromEmail.trim() : "";
        String pwd = mailPassword != null ? mailPassword.replaceAll("\\s+", "") : "";
        if (!StringUtils.hasText(user)) {
            throw new IllegalStateException("SMTP username is not configured. Please set EMAIL_USER (or EMAIL_USER_BREVO) in Render environment variables.");
        }
        if (!StringUtils.hasText(pwd)) {
            throw new IllegalStateException("SMTP password / key is not configured. Please set EMAIL_PASS (or EMAIL_PASS_BREVO) in Render environment variables.");
        }

        String from = determineSender(user, senderEmail);

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            if (StringUtils.hasText(from)) {
                mail.setFrom(from);
            }
            mail.setTo(toEmail);
            mail.setSubject(subject);
            mail.setText(text);
            log.info("Sending email to {} (from: {})", toEmail, from);
            mailSender.send(mail);
            log.info("Email sent successfully to {}", toEmail);
        } catch (MailException e) {
            log.error("SMTP send failed to {} (from: {}): {}", toEmail, from, e.getMessage(), e);
            throw e;
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
