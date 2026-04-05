package com.placementportal.repository;

import com.placementportal.model.JobPosting;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobPostingRepository extends MongoRepository<JobPosting, String> {

    List<JobPosting> findByActiveTrueOrderByCreatedAtDesc();

    Page<JobPosting> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<JobPosting> findByActiveTrueAndAudienceTagOrderByCreatedAtDesc(String audienceTag, Pageable pageable);

    List<JobPosting> findAllByOrderByCreatedAtDesc();
}
