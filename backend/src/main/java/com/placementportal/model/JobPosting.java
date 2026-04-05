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

@Document(collection = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    private String id;

    private String title;

    private String companyName;

    private String description;

    private String applyLink;

    private String location;

    /** e.g. full-time, intern, contract */
    private String jobType;

    private String skillsRequired;

    /** Graduation / passout years (e.g. 2025, 2025–2026). */
    private String passoutYear;

    /** Eligibility: degree major (e.g. CSE). */
    private String qualificationMajor;

    /** Eligibility: branch or specialization. */
    private String qualificationBranch;

    /** Eligibility: graduating year or batch (e.g. 2025). */
    private String qualificationYear;

    /** Fresher, or experience summary (e.g. 3 years Java). */
    private String experienceText;

    /** FRESHERS or EXPERIENCED — used for student filters. */
    private String audienceTag;

    /** Human-readable posted date or deadline line. */
    private String postedOn;

    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt;
}
