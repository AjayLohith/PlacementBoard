package com.placementportal.service;

import com.placementportal.dto.JobPostingRequest;
import com.placementportal.dto.JobPostingResponse;
import com.placementportal.exception.ResourceNotFoundException;
import com.placementportal.model.JobPosting;
import com.placementportal.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public List<JobPostingResponse> listActive() {
        return jobPostingRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<JobPostingResponse> listAllForAdmin() {
        return jobPostingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public JobPostingResponse create(JobPostingRequest request) {
        JobPosting j = JobPosting.builder()
                .title(request.getTitle().trim())
                .companyName(trimOrNull(request.getCompanyName()))
                .description(request.getDescription().trim())
                .applyLink(trimOrNull(request.getApplyLink()))
                .location(trimOrNull(request.getLocation()))
                .jobType(trimOrNull(request.getJobType()))
                .skillsRequired(trimOrNull(request.getSkillsRequired()))
                .passoutYear(trimOrNull(request.getPassoutYear()))
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
        j.setPostedOn(trimOrNull(request.getPostedOn()));
        return toResponse(jobPostingRepository.save(j));
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
                .postedOn(j.getPostedOn())
                .active(j.getActive())
                .createdAt(j.getCreatedAt())
                .updatedAt(j.getUpdatedAt())
                .build();
    }
}
