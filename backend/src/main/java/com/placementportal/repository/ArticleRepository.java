package com.placementportal.repository;

import com.placementportal.model.Article;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleRepository extends MongoRepository<Article, String> {

    List<Article> findByPublishedTrueOrderByPublishedAtDesc();

    Page<Article> findByPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    List<Article> findAllByOrderByCreatedAtDesc();

    Optional<Article> findBySlugAndPublishedTrue(String slug);

    Optional<Article> findBySlug(String slug);
}
