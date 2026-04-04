package com.placementportal.repository;

import com.placementportal.model.JobPosting;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobPostingRepository extends MongoRepository<JobPosting, String> {

    List<JobPosting> findByActiveTrueOrderByCreatedAtDesc();

    List<JobPosting> findAllByOrderByCreatedAtDesc();
}
