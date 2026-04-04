package com.placementportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleAiFillResponse {

    private String title;
    private String slug;
    private String excerpt;
    private String body;
    private Boolean published;
}
