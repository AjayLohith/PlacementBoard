package com.placementportal.service;

import com.placementportal.dto.ArticleDetailResponse;
import com.placementportal.dto.ArticleRequest;
import com.placementportal.dto.ArticleSummaryResponse;
import com.placementportal.exception.ResourceNotFoundException;
import com.placementportal.exception.ValidationException;
import com.placementportal.model.Article;
import com.placementportal.repository.ArticleRepository;
import com.placementportal.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<ArticleSummaryResponse> listPublishedSummaries() {
        return articleRepository.findByPublishedTrueOrderByPublishedAtDesc().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    public ArticleDetailResponse getPublishedBySlug(String slug) {
        Article a = articleRepository.findBySlugAndPublishedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        return toDetail(a);
    }

    public List<ArticleDetailResponse> listAllForAdmin() {
        return articleRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDetail)
                .collect(Collectors.toList());
    }

    public ArticleDetailResponse create(ArticleRequest request) {
        String slug = resolveUniqueSlug(request.getSlug(), request.getTitle(), null);
        boolean published = Boolean.TRUE.equals(request.getPublished());
        Instant now = Instant.now();
        Article a = Article.builder()
                .title(request.getTitle().trim())
                .slug(slug)
                .excerpt(trimOrNull(request.getExcerpt()))
                .body(request.getBody().trim())
                .published(published)
                .publishedAt(published ? now : null)
                .build();
        return toDetail(articleRepository.save(a));
    }

    public ArticleDetailResponse update(String id, ArticleRequest request) {
        Article a = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        String newSlug = resolveUniqueSlug(request.getSlug(), request.getTitle(), a.getId());
        a.setTitle(request.getTitle().trim());
        a.setSlug(newSlug);
        a.setExcerpt(trimOrNull(request.getExcerpt()));
        a.setBody(request.getBody().trim());
        if (request.getPublished() != null) {
            a.setPublished(request.getPublished());
            if (Boolean.TRUE.equals(request.getPublished()) && a.getPublishedAt() == null) {
                a.setPublishedAt(Instant.now());
            }
            if (Boolean.FALSE.equals(request.getPublished())) {
                a.setPublishedAt(null);
            }
        }
        return toDetail(articleRepository.save(a));
    }

    public void delete(String id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Article not found");
        }
        articleRepository.deleteById(id);
    }

    private String resolveUniqueSlug(String requestedSlug, String title, String excludeId) {
        String base = requestedSlug != null && !requestedSlug.isBlank()
                ? SlugUtil.slugify(requestedSlug)
                : SlugUtil.slugify(title);
        String slug = base;
        int suffix = 2;
        while (true) {
            var existing = articleRepository.findBySlug(slug);
            if (existing.isEmpty()
                    || (excludeId != null && excludeId.equals(existing.get().getId()))) {
                return slug;
            }
            slug = base + "-" + suffix++;
            if (suffix > 1000) {
                throw new ValidationException("Could not generate unique slug");
            }
        }
    }

    private static String trimOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private ArticleSummaryResponse toSummary(Article a) {
        return ArticleSummaryResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .slug(a.getSlug())
                .excerpt(a.getExcerpt())
                .publishedAt(a.getPublishedAt())
                .build();
    }

    private ArticleDetailResponse toDetail(Article a) {
        return ArticleDetailResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .slug(a.getSlug())
                .excerpt(a.getExcerpt())
                .body(a.getBody())
                .published(a.getPublished())
                .publishedAt(a.getPublishedAt())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
