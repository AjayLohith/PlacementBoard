package com.placementportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingResponse {

    @JsonProperty("_id")
    private String id;

    private String title;
    private String companyName;
    private String description;
    private String applyLink;
    private String location;
    private String jobType;
    private String skillsRequired;
    private String passoutYear;

    private String qualificationMajor;
    private String qualificationBranch;
    private String qualificationYear;
    private String experienceText;
    private String audienceTag;

    private String postedOn;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
