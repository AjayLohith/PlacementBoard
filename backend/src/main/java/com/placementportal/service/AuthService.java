package com.placementportal.service;

import com.placementportal.dto.AuthResponse;
import com.placementportal.dto.RegisterRequest;
import com.placementportal.dto.ResetPasswordRequest;
import com.placementportal.exception.UnauthorizedException;
import com.placementportal.exception.ValidationException;
import com.placementportal.model.User;
import com.placementportal.repository.UserRepository;
import com.placementportal.security.JwtService;
import com.placementportal.util.SmtpErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthResponse registerUser(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("User already exists");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId());

        return AuthResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .token(token)
                .build();
    }

    public AuthResponse authUser(String email, String password) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getId());
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    /**
     * Sends a 6-digit OTP to the user's email when the account exists.
     * Always succeeds from the client's perspective to avoid email enumeration.
     */
    public void requestPasswordResetOtp(String rawEmail) {
        String email = rawEmail.trim().toLowerCase();
        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            int n = SECURE_RANDOM.nextInt(1_000_000);
            String otp = String.format("%06d", n);
            user.setResetPasswordToken(sha256Hex(otp));
            user.setResetPasswordExpire(Instant.now().plus(10, ChronoUnit.MINUTES));
            userRepository.save(user);
            try {
                emailService.sendPasswordResetOtp(user.getEmail(), otp);
            } catch (Exception e) {
                log.error("OTP email failed for {}", email, e);
                String hint = SmtpErrorMessages.friendly(e);
                throw new ValidationException("Could not send email: " + hint);
            }
        }, () -> log.debug("Password reset requested for unknown email (no OTP sent)"));
    }

    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Invalid or expired code"));

        String hashed = sha256Hex(request.getOtp().trim());
        if (user.getResetPasswordToken() == null || !user.getResetPasswordToken().equals(hashed)) {
            throw new ValidationException("Invalid or expired code");
        }
        if (user.getResetPasswordExpire() == null || user.getResetPasswordExpire().isBefore(Instant.now())) {
            throw new ValidationException("Invalid or expired code");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpire(null);
        userRepository.save(user);
    }

    private static String sha256Hex(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Error hashing OTP", e);
        }
    }
}
