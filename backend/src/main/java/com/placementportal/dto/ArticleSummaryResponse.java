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
public class ArticleSummaryResponse {

    @JsonProperty("_id")
    private String id;

    private String title;
    private String slug;
    private String excerpt;
    private Instant publishedAt;
}
