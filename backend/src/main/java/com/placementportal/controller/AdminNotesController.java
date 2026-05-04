package com.placementportal.controller;

import com.placementportal.model.AdminNotes;
import com.placementportal.repository.AdminNotesRepository;
import com.placementportal.security.AdminAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/notes")
@RequiredArgsConstructor
public class AdminNotesController {

    private final AdminNotesRepository adminNotesRepository;
    private final AdminAuthorizationService adminAuthorizationService;

    private static final String NOTES_ID = "singleton";

    @GetMapping
    public ResponseEntity<AdminNotes> getNotes() {
        adminAuthorizationService.requireAdmin();
        Optional<AdminNotes> notes = adminNotesRepository.findById(NOTES_ID);
        return notes
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(AdminNotes.builder().id(NOTES_ID).content("").build()));
    }

    @PutMapping
    public ResponseEntity<AdminNotes> updateNotes(@RequestBody AdminNotes request) {
        adminAuthorizationService.requireAdmin();
        AdminNotes notes = request;
        notes.setId(NOTES_ID);
        AdminNotes saved = adminNotesRepository.save(notes);
        return ResponseEntity.ok(saved);
    }
}
