package com.placementportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.placementportal.model.Round;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceResponse {

    @JsonProperty("_id")
    private String id;

    private UserSummary user;
    private CompanySummary company;
    private String postTitle;
    private Date interviewDate;
    private List<Round> interviewRounds;
    private String suggestions;
    private String additionalInfo;
    private String closingNote;
    private Boolean isApproved;

    private Boolean rejected;

    private String rejectionNote;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserSummary {

        @JsonProperty("_id")
        private String id;

        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanySummary {

        @JsonProperty("_id")
        private String id;

        private String name;
    }
}
