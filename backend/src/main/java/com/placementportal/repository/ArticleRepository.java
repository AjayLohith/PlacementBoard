package com.placementportal.repository;

import com.placementportal.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {

    List<Article> findByPublishedTrueOrderByPublishedAtDesc();

    List<Article> findAllByOrderByCreatedAtDesc();

    Optional<Article> findBySlugAndPublishedTrue(String slug);

    Optional<Article> findBySlug(String slug);
}
