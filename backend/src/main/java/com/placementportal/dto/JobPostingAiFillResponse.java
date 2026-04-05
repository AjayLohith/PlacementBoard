package com.placementportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Parsed job fields from admin AI (maps to job board + extended fields). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JobPostingAiFillResponse {

    private String title;
    private String companyName;
    private String description;
    private String applyLink;
    private String location;
    private String jobType;
    /** Skills, tools, tech stack — clear comma or line-separated list. */
    private String skillsRequired;
    /** Target graduation / passout years (e.g. "2025", "2025–2026"). */
    private String passoutYear;

    private String qualificationMajor;
    private String qualificationBranch;
    private String qualificationYear;
    private String experienceText;
    private String audienceTag;

    /** Posted date or application deadline as short text (e.g. "Posted Jan 2026"). */
    private String postedOn;
}
