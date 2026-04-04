package com.placementportal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Document(collection = "experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    private String id;

    @Field("user")
    private String userId;

    @Field("company")
    private String companyId;

    private String postTitle;

    private Date interviewDate;

    private List<Round> interviewRounds;

    private String suggestions;

    private String additionalInfo;

    private String closingNote;

    @Builder.Default
    private Boolean isApproved = false;

    /** When true, hidden from pending queue and never shown publicly. */
    @Builder.Default
    private Boolean rejected = false;

    private String rejectionNote;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt;
}
