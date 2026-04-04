package com.placementportal.service;

import com.placementportal.dto.CompanyResponse;
import com.placementportal.model.Company;
import com.placementportal.model.Experience;
import com.placementportal.repository.CompanyRepository;
import com.placementportal.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ExperienceRepository experienceRepository;

    /**
     * Companies that have at least one approved experience — not the full {@code companies} collection.
     * If the UI shows names you do not see in Compass, check the same DB/URI as the app and the {@code experiences} collection.
     */
    public List<CompanyResponse> getCompaniesWithApprovedExperiences() {
        List<Experience> approved = experienceRepository.findByIsApprovedTrue();
        Set<String> ids = new LinkedHashSet<>();
        for (Experience e : approved) {
            if (e.getCompanyId() != null) {
                ids.add(e.getCompanyId());
            }
        }
        return companyRepository.findAllById(ids).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Company getCompanyBySlug(String slug) {
        return companyRepository.findBySlug(slug).orElse(null);
    }

    public Company createOrGetCompany(String companyName) {
        String name = companyName.trim();
        return companyRepository.findByName(name).orElseGet(() -> {
            String baseSlug = slugify(name);
            String slug = ensureUniqueSlug(baseSlug, name);
            String letter = name.isEmpty() ? "?" : String.valueOf(Character.toUpperCase(name.charAt(0)));
            String encoded = URLEncoder.encode(letter, StandardCharsets.UTF_8);
            String logo = "https://placehold.co/80x80/e2e8f0/334155?text=" + encoded;
            Company company = Company.builder()
                    .name(name)
                    .slug(slug)
                    .logo(logo)
                    .build();
            return companyRepository.save(company);
        });
    }

    private String ensureUniqueSlug(String baseSlug, String name) {
        String slug = baseSlug;
        int n = 1;
        while (companyRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + n++;
        }
        return slug;
    }

    public static String slugify(String name) {
        String slug = name.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", "-");
        slug = slug.replaceAll("[^a-z0-9-]", "");
        slug = slug.replaceAll("-+", "-").replaceAll("^-|-$", "");
        return slug.isEmpty() ? "company" : slug;
    }

    private CompanyResponse toResponse(Company c) {
        return CompanyResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .logo(c.getLogo())
                .build();
    }
}
