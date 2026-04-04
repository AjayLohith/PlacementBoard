package com.placementportal.repository;

import com.placementportal.model.Experience;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ExperienceRepository extends MongoRepository<Experience, String> {

    List<Experience> findByCompanyIdAndIsApprovedTrue(String companyId);

    List<Experience> findByIsApprovedFalse();

    /** Pending queue: not approved and not explicitly rejected (handles missing rejected field). */
    @Query("{ 'isApproved': false, $or: [ { 'rejected': false }, { 'rejected': null }, { 'rejected': { $exists: false } } ] }")
    List<Experience> findPendingForModeration();

    List<Experience> findByIsApprovedTrue();

    long countByCompanyId(String companyId);
}
