package com.placementportal.controller;

import com.placementportal.model.AdminNotes;
import com.placementportal.repository.AdminNotesRepository;
import com.placementportal.security.AdminAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin/notes")
@RequiredArgsConstructor
public class AdminNotesController {

    private final AdminNotesRepository adminNotesRepository;
    private final AdminAuthorizationService adminAuthorizationService;

    @GetMapping
    public ResponseEntity<List<AdminNotes>> getNotes() {
        adminAuthorizationService.requireAdmin();
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return ResponseEntity.ok(adminNotesRepository.findAll(sort));
    }

    @PostMapping
    public ResponseEntity<AdminNotes> createNotes(@RequestBody CreateAdminNoteRequest request) {
        adminAuthorizationService.requireAdmin();
        String content = trimToNull(request == null ? null : request.content());
        if (content == null) {
            throw new BadRequestException("Content is required");
        }
        String title = trimToNull(request.title());
        if (title == null) {
            title = buildFallbackTitle(content);
        }
        Instant now = Instant.now();
        AdminNotes notes = AdminNotes.builder()
                .title(title)
                .content(content)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return ResponseEntity.ok(adminNotesRepository.save(notes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotes(@PathVariable String id) {
        adminAuthorizationService.requireAdmin();
        adminNotesRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String buildFallbackTitle(String content) {
        String firstLine = content.lines().findFirst().orElse("").trim();
        if (firstLine.isEmpty()) {
            return "Admin note";
        }
        String collapsed = firstLine.replaceAll("\\s+", " ");
        if (collapsed.length() <= 48) {
            return collapsed;
        }
        return collapsed.substring(0, 45).trim() + "...";
    }

    private record CreateAdminNoteRequest(String title, String content) {}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private static class BadRequestException extends RuntimeException {
        private BadRequestException(String message) {
            super(message);
        }
    }
}
