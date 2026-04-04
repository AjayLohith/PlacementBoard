package com.placementportal.dto;

import com.placementportal.model.Round;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceRequest {

    @NotBlank(message = "Post title is required")
    private String postTitle;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotNull(message = "Interview date is required")
    private Date interviewDate;

    private List<Round> interviewRounds;
    private String suggestions;
    private String additionalInfo;
    private String closingNote;
}
