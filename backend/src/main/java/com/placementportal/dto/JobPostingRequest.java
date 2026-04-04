package com.placementportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String companyName;

    @NotBlank(message = "Description is required")
    private String description;

    private String applyLink;

    private String location;

    private String jobType;

    private String skillsRequired;

    private String passoutYear;

    private String postedOn;

    private Boolean active;
}
