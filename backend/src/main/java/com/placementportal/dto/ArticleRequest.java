package com.placementportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String slug;

    private String excerpt;

    @NotBlank(message = "Body is required")
    private String body;

    private Boolean published;
}
