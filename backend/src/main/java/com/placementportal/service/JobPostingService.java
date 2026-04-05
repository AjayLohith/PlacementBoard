package com.placementportal.service;

import com.placementportal.dto.JobPostingRequest;
import com.placementportal.dto.JobPostingResponse;
import com.placementportal.dto.PagedResponse;
import com.placementportal.exception.ResourceNotFoundException;
import com.placementportal.model.JobPosting;
import com.placementportal.repository.JobPostingRepository;
import com.placementportal.util.JobAudienceTagResolver;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public PagedResponse<JobPostingResponse> listActivePaged(int page, int size, String audienceFilter) {
        int s = Math.min(Math.max(size, 1), 50);
        int p = Math.max(page, 0);
        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createdAt"));
        String filter = audienceFilter != null ? audienceFilter.trim() : "ALL";
        Page<JobPosting> result;
        if ("ALL".equalsIgnoreCase(filter)) {
            result = jobPostingRepository.findByActiveTrueOrderByCreatedAtDesc(pageable);
        } else if (JobAudienceTagResolver.FRESHERS.equalsIgnoreCase(filter)
                || JobAudienceTagResolver.EXPERIENCED.equalsIgnoreCase(filter)) {
            String tag = filter.toUpperCase();
            result = jobPostingRepository.findByActiveTrueAndAudienceTagOrderByCreatedAtDesc(tag, pageable);
        } else {
            result = jobPostingRepository.findByActiveTrueOrderByCreatedAtDesc(pageable);
        }
        return PagedResponse.<JobPostingResponse>builder()
                .content(result.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(result.getNumber())
                .size(result.getSize())
                .build();
    }

    public List<JobPostingResponse> listAllForAdmin() {
        return jobPostingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public JobPostingResponse create(JobPostingRequest request) {
        String tag = resolveAudienceTag(request);
        JobPosting j = JobPosting.builder()
                .title(request.getTitle().trim())
                .companyName(trimOrNull(request.getCompanyName()))
                .description(request.getDescription().trim())
                .applyLink(trimOrNull(request.getApplyLink()))
                .location(trimOrNull(request.getLocation()))
                .jobType(trimOrNull(request.getJobType()))
                .skillsRequired(trimOrNull(request.getSkillsRequired()))
                .passoutYear(trimOrNull(request.getPassoutYear()))
                .qualificationMajor(trimOrNull(request.getQualificationMajor()))
                .qualificationBranch(trimOrNull(request.getQualificationBranch()))
                .qualificationYear(trimOrNull(request.getQualificationYear()))
                .experienceText(trimOrNull(request.getExperienceText()))
                .audienceTag(tag)
                .postedOn(trimOrNull(request.getPostedOn()))
                .active(request.getActive() == null || request.getActive())
                .build();
        return toResponse(jobPostingRepository.save(j));
    }

    public JobPostingResponse update(String id, JobPostingRequest request) {
        JobPosting j = jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));
        j.setTitle(request.getTitle().trim());
        j.setCompanyName(trimOrNull(request.getCompanyName()));
        j.setDescription(request.getDescription().trim());
        j.setApplyLink(trimOrNull(request.getApplyLink()));
        j.setLocation(trimOrNull(request.getLocation()));
        j.setJobType(trimOrNull(request.getJobType()));
        if (request.getActive() != null) {
            j.setActive(request.getActive());
        }
        j.setSkillsRequired(trimOrNull(request.getSkillsRequired()));
        j.setPassoutYear(trimOrNull(request.getPassoutYear()));
        j.setQualificationMajor(trimOrNull(request.getQualificationMajor()));
        j.setQualificationBranch(trimOrNull(request.getQualificationBranch()));
        j.setQualificationYear(trimOrNull(request.getQualificationYear()));
        j.setExperienceText(trimOrNull(request.getExperienceText()));
        j.setAudienceTag(resolveAudienceTag(request));
        j.setPostedOn(trimOrNull(request.getPostedOn()));
        return toResponse(jobPostingRepository.save(j));
    }

    private static String resolveAudienceTag(JobPostingRequest request) {
        return JobAudienceTagResolver.resolve(
                request.getAudienceTag(),
                request.getExperienceText(),
                request.getPassoutYear(),
                request.getQualificationYear());
    }

    public void delete(String id) {
        if (!jobPostingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job posting not found");
        }
        jobPostingRepository.deleteById(id);
    }

    private static String trimOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private JobPostingResponse toResponse(JobPosting j) {
        String displayTag = j.getAudienceTag();
        if (displayTag == null || displayTag.isBlank()) {
            displayTag = JobAudienceTagResolver.resolve(
                    null, j.getExperienceText(), j.getPassoutYear(), j.getQualificationYear());
        }
        return JobPostingResponse.builder()
                .id(j.getId())
                .title(j.getTitle())
                .companyName(j.getCompanyName())
                .description(j.getDescription())
                .applyLink(j.getApplyLink())
                .location(j.getLocation())
                .jobType(j.getJobType())
                .skillsRequired(j.getSkillsRequired())
                .passoutYear(j.getPassoutYear())
                .qualificationMajor(j.getQualificationMajor())
                .qualificationBranch(j.getQualificationBranch())
                .qualificationYear(j.getQualificationYear())
                .experienceText(j.getExperienceText())
                .audienceTag(displayTag)
                .postedOn(j.getPostedOn())
                .active(j.getActive())
                .createdAt(j.getCreatedAt())
                .updatedAt(j.getUpdatedAt())
                .build();
    }
}
