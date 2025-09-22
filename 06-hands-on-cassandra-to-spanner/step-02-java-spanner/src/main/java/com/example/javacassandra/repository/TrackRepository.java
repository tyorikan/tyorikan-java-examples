package com.example.javacassandra.repository;

import com.example.javacassandra.model.Track;
import com.google.cloud.spanner.Key;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Track entities.
 * The primary key is a composite key, so we use `com.google.cloud.spanner.Key`.
 */
@Repository
public interface TrackRepository extends SpannerRepository<Track, Key> {
}
