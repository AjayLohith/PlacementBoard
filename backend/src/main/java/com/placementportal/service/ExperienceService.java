package com.placementportal.service;

import com.placementportal.dto.ExperienceRequest;
import com.placementportal.dto.ExperienceResponse;
import com.placementportal.exception.ResourceNotFoundException;
import com.placementportal.mapper.ExperienceMapper;
import com.placementportal.model.Company;
import com.placementportal.model.Experience;
import com.placementportal.model.User;
import com.placementportal.repository.CompanyRepository;
import com.placementportal.repository.ExperienceRepository;
import com.placementportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final ExperienceMapper experienceMapper;

    public ExperienceResponse createExperience(String userId, ExperienceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Company company = companyService.createOrGetCompany(request.getCompanyName());

        Experience experience = Experience.builder()
                .userId(user.getId())
                .companyId(company.getId())
                .postTitle(request.getPostTitle())
                .interviewDate(request.getInterviewDate())
                .interviewRounds(request.getInterviewRounds())
                .suggestions(request.getSuggestions())
                .additionalInfo(request.getAdditionalInfo())
                .closingNote(request.getClosingNote())
                .isApproved(false)
                .build();

        Experience saved = experienceRepository.save(experience);
        return enrichResponse(saved, user, company);
    }

    public List<ExperienceResponse> getExperiencesByCompanySlug(String slug) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return experienceRepository.findByCompanyIdAndIsApprovedTrue(company.getId()).stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    public List<ExperienceResponse> getPendingExperiences() {
        return experienceRepository.findPendingForModeration().stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    public List<ExperienceResponse> getApprovedExperiences() {
        return experienceRepository.findByIsApprovedTrue().stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    public ExperienceResponse approveExperience(String experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
        experience.setIsApproved(true);
        experience.setRejected(false);
        experience.setRejectionNote(null);
        Experience saved = experienceRepository.save(experience);
        return enrichResponse(saved);
    }

    public ExperienceResponse rejectExperience(String experienceId, String note) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
        experience.setIsApproved(false);
        experience.setRejected(true);
        experience.setRejectionNote(note != null && !note.isBlank() ? note.trim() : null);
        Experience saved = experienceRepository.save(experience);
        return enrichResponse(saved);
    }

    public void deleteExperience(String experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
        String companyId = experience.getCompanyId();
        experienceRepository.deleteById(experienceId);
        if (companyId != null && experienceRepository.countByCompanyId(companyId) == 0) {
            companyRepository.deleteById(companyId);
            log.info("Removed company {} (no experiences left)", companyId);
        }
    }

    private ExperienceResponse enrichResponse(Experience e) {
        User user = userRepository.findById(e.getUserId())
                .orElse(User.builder().id(e.getUserId()).name("Unknown").build());
        Company company = companyRepository.findById(e.getCompanyId())
                .orElse(Company.builder().id(e.getCompanyId()).name("Unknown").build());
        return enrichResponse(e, user, company);
    }

    private ExperienceResponse enrichResponse(Experience e, User user, Company company) {
        ExperienceResponse r = experienceMapper.toResponse(e);
        r.setUser(ExperienceResponse.UserSummary.builder().id(user.getId()).name(user.getName()).build());
        r.setCompany(ExperienceResponse.CompanySummary.builder()
                .id(company.getId())
                .name(company.getName())
                .build());
        return r;
    }
}
