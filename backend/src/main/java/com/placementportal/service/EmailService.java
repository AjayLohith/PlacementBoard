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

    @Value("${app.email.from-address:ajjgamerr@gmail.com}")
    private String senderEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public void sendEmail(String toEmail, String subject, String text) {
        String user = fromEmail != null ? fromEmail.trim() : "";
        String pwd = mailPassword != null ? mailPassword.replaceAll("\\s+", "") : "";
        if (!StringUtils.hasText(user)) {
            throw new IllegalStateException("EMAIL_USER / spring.mail.username is not set");
        }
        if (!StringUtils.hasText(pwd)) {
            throw new IllegalStateException("EMAIL_PASS / spring.mail.password is not set (use a Gmail App Password)");
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            // Use verified sender address instead of the authentication username if it's a provider like Brevo
            mail.setFrom(senderEmail);
            mail.setTo(toEmail);
            mail.setSubject(subject);
            mail.setText(text);
            mailSender.send(mail);
            log.info("Email sent to {}", toEmail);
        } catch (MailException e) {
            log.error("SMTP send failed to {}", toEmail, e);
            throw e;
        }
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
