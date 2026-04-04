package com.placementportal.repository;

import com.placementportal.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findByName(String name);

    Optional<Company> findBySlug(String slug);

    boolean existsByName(String name);
}
