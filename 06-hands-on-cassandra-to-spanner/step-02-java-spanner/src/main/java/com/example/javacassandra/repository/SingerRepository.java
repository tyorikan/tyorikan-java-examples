package com.example.javacassandra.repository;

import com.example.javacassandra.model.Singer;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Singer entities.
 */
@Repository
public interface SingerRepository extends SpannerRepository<Singer, String> {
}
