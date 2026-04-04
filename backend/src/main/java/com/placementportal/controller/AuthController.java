package com.placementportal.controller;

import com.placementportal.dto.AuthResponse;
import com.placementportal.dto.EmailOnlyRequest;
import com.placementportal.dto.LoginRequest;
import com.placementportal.dto.RegisterRequest;
import com.placementportal.dto.ResetPasswordRequest;
import com.placementportal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("Register: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Login: {}", request.getEmail());
        return ResponseEntity.ok(authService.authUser(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody EmailOnlyRequest body) {
        log.debug("Forgot-password OTP requested for: {}", body.getEmail());
        authService.requestPasswordResetOtp(body.getEmail());
        Map<String, String> res = new LinkedHashMap<>();
        res.put("message", "If an account exists for this email, a reset code has been sent.");
        return ResponseEntity.ok(res);
    }

    @PutMapping("/resetpassword")
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.debug("Reset password with OTP");
        authService.resetPassword(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Password has been reset successfully");
        return ResponseEntity.ok(body);
    }
}
