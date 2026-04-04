package com.placementportal.controller;

import com.placementportal.dto.ExperienceRequest;
import com.placementportal.dto.ExperienceResponse;
import com.placementportal.dto.RejectExperienceRequest;
import com.placementportal.security.AdminAuthorizationService;
import com.placementportal.service.ExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/experiences")
@Slf4j
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;
    private final AdminAuthorizationService adminAuthorizationService;

    @PostMapping
    public ResponseEntity<ExperienceResponse> create(@Valid @RequestBody ExperienceRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        log.debug("Create experience for user {}", userId);
        ExperienceResponse created = experienceService.createExperience(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{companySlug}")
    public ResponseEntity<List<ExperienceResponse>> byCompany(@PathVariable String companySlug) {
        log.debug("Experiences for slug {}", companySlug);
        return ResponseEntity.ok(experienceService.getExperiencesByCompanySlug(companySlug));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<ExperienceResponse>> pending() {
        adminAuthorizationService.requireAdmin();
        log.debug("Admin: pending experiences");
        return ResponseEntity.ok(experienceService.getPendingExperiences());
    }

    @GetMapping("/admin/approved")
    public ResponseEntity<List<ExperienceResponse>> approved() {
        adminAuthorizationService.requireAdmin();
        log.debug("Admin: approved experiences");
        return ResponseEntity.ok(experienceService.getApprovedExperiences());
    }

    @PutMapping("/admin/{id}/approve")
    public ResponseEntity<ExperienceResponse> approve(@PathVariable String id) {
        adminAuthorizationService.requireAdmin();
        log.debug("Admin: approve {}", id);
        return ResponseEntity.ok(experienceService.approveExperience(id));
    }

    @PutMapping("/admin/{id}/reject")
    public ResponseEntity<ExperienceResponse> reject(
            @PathVariable String id,
            @RequestBody(required = false) RejectExperienceRequest body) {
        adminAuthorizationService.requireAdmin();
        String note = body != null ? body.getNote() : null;
        log.debug("Admin: reject {}", id);
        return ResponseEntity.ok(experienceService.rejectExperience(id, note));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        adminAuthorizationService.requireAdmin();
        log.debug("Admin: delete {}", id);
        experienceService.deleteExperience(id);
        return ResponseEntity.ok(Map.of("message", "Experience removed."));
    }
}
