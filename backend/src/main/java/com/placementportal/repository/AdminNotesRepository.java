package com.placementportal.repository;

import com.placementportal.model.AdminNotes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminNotesRepository extends MongoRepository<AdminNotes, String> {
}
