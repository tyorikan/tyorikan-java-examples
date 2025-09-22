package com.example.javacassandra.repository;

import com.example.javacassandra.model.Album;
import com.google.cloud.spanner.Key;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Album entities.
 * The primary key is a composite key, so we use `com.google.cloud.spanner.Key`.
 */
@Repository
public interface AlbumRepository extends SpannerRepository<Album, Key> {
}
