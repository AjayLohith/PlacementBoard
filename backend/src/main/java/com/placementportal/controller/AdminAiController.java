package com.placementportal.controller;

import com.placementportal.dto.AiRawTextRequest;
import com.placementportal.dto.ArticleAiFillResponse;
import com.placementportal.dto.JobPostingAiFillResponse;
import com.placementportal.security.AdminAuthorizationService;
import com.placementportal.service.AdminAiParseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/ai")
@RequiredArgsConstructor
public class AdminAiController {

    private final AdminAiParseService adminAiParseService;
    private final AdminAuthorizationService adminAuthorizationService;

    @PostMapping("/parse-job")
    public JobPostingAiFillResponse parseJob(@Valid @RequestBody AiRawTextRequest body) {
        adminAuthorizationService.requireAdmin();
        return adminAiParseService.parseJobPosting(body.getRawText());
    }

    @PostMapping("/parse-article")
    public ArticleAiFillResponse parseArticle(@Valid @RequestBody AiRawTextRequest body) {
        adminAuthorizationService.requireAdmin();
        return adminAiParseService.parseArticle(body.getRawText());
    }
}
