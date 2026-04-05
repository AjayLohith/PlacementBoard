package com.placementportal.controller;

import com.placementportal.dto.JobPostingRequest;
import com.placementportal.dto.JobPostingResponse;
import com.placementportal.dto.PagedResponse;
import com.placementportal.security.AdminAuthorizationService;
import com.placementportal.service.JobPostingService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/jobs")
@Slf4j
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;
    private final AdminAuthorizationService adminAuthorizationService;

    @GetMapping
    public ResponseEntity<PagedResponse<JobPostingResponse>> listPublic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "ALL") String audience) {
        return ResponseEntity.ok(jobPostingService.listActivePaged(page, size, audience));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<JobPostingResponse>> listAdmin() {
        adminAuthorizationService.requireAdmin();
        return ResponseEntity.ok(jobPostingService.listAllForAdmin());
    }

    @PostMapping("/admin")
    public ResponseEntity<JobPostingResponse> create(@Valid @RequestBody JobPostingRequest request) {
        adminAuthorizationService.requireAdmin();
        JobPostingResponse created = jobPostingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<JobPostingResponse> update(
            @PathVariable String id, @Valid @RequestBody JobPostingRequest request) {
        adminAuthorizationService.requireAdmin();
        return ResponseEntity.ok(jobPostingService.update(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        adminAuthorizationService.requireAdmin();
        jobPostingService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Job posting removed."));
    }
}
