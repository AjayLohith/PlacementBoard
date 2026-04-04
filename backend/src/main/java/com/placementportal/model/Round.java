package com.placementportal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Round {

    @Field("roundName")
    private String roundName;

    @Field("duration")
    private String duration;

    @Field("method")
    private String method;

    @Field("focus")
    private String focus;

    @Field("keyQuestions")
    private String keyQuestions;

    @Field("obstacles")
    private String obstacles;
}
