package com.placementportal.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
@Slf4j
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(
            @Value("${spring.mail.host:}") String host,
            @Value("${spring.mail.port:587}") int port,
            @Value("${spring.mail.username:}") String username,
            @Value("${spring.mail.password:}") String password) {

        String user = username != null ? username.trim() : "";
        String pass = password != null ? password.replaceAll("\\s+", "") : "";

        // Smart host resolution if not explicitly set
        String resolvedHost = host != null ? host.trim() : "";
        if (!StringUtils.hasText(resolvedHost)) {
            if (user.endsWith("@gmail.com")) {
                resolvedHost = "smtp.gmail.com";
            } else {
                resolvedHost = "smtp-relay.brevo.com";
            }
        }

        int resolvedPort = port > 0 ? port : 587;
        // Fix for accidental port 2525 when using Gmail or restricted cloud ports
        if ("smtp.gmail.com".equalsIgnoreCase(resolvedHost) && resolvedPort == 2525) {
            resolvedPort = 587;
        }

        log.info("Configuring JavaMailSender: host={}, port={}, username={}",
                resolvedHost, resolvedPort, mask(user));

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(resolvedHost);
        sender.setPort(resolvedPort);
        sender.setUsername(user);
        sender.setPassword(pass);
        sender.setDefaultEncoding("UTF-8");

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.writetimeout", "15000");
        props.put("mail.smtp.ssl.trust", "*");

        if (resolvedPort == 465) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        return sender;
    }

    private static String mask(String user) {
        if (!StringUtils.hasText(user)) return "<not-set>";
        int at = user.indexOf('@');
        if (at > 2) {
            return user.substring(0, 2) + "***" + user.substring(at);
        }
        return user.length() > 3 ? user.substring(0, 3) + "***" : "***";
    }
}