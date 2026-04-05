package com.placementportal.controller;

import com.placementportal.dto.ArticleDetailResponse;
import com.placementportal.dto.ArticleRequest;
import com.placementportal.dto.ArticleSummaryResponse;
import com.placementportal.dto.PagedResponse;
import com.placementportal.security.AdminAuthorizationService;
import com.placementportal.service.ArticleService;
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
@RequestMapping("/api/articles")
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final AdminAuthorizationService adminAuthorizationService;

    @GetMapping
    public ResponseEntity<PagedResponse<ArticleSummaryResponse>> listPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(articleService.listPublishedSummariesPaged(page, size));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ArticleDetailResponse> bySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getPublishedBySlug(slug));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<ArticleDetailResponse>> listAdmin() {
        adminAuthorizationService.requireAdmin();
        return ResponseEntity.ok(articleService.listAllForAdmin());
    }

    @PostMapping("/admin")
    public ResponseEntity<ArticleDetailResponse> create(@Valid @RequestBody ArticleRequest request) {
        adminAuthorizationService.requireAdmin();
        ArticleDetailResponse created = articleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ArticleDetailResponse> update(
            @PathVariable String id, @Valid @RequestBody ArticleRequest request) {
        adminAuthorizationService.requireAdmin();
        return ResponseEntity.ok(articleService.update(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        adminAuthorizationService.requireAdmin();
        articleService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Article removed."));
    }
}
