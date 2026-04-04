package com.placementportal.security;

import com.placementportal.config.AppProperties;
import com.placementportal.exception.ForbiddenException;
import com.placementportal.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AdminAuthorizationService {

    private final AppProperties appProperties;

    public void requireAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new UnauthorizedException("Sign in required");
        }
        boolean allowed = Arrays.stream(appProperties.getAdminEmails())
                .anyMatch(e -> e.equalsIgnoreCase(principal.getEmail()));
        if (!allowed) {
            throw new ForbiddenException("Not authorized as an admin");
        }
    }
}
