package com.example.javacassandra.repository;

import java.util.List;
import java.util.UUID;

import com.example.javacassandra.model.Track;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Track entities.
 */
@Repository
public interface TrackRepository extends CassandraRepository<Track, UUID> {

    /**
     * Finds all tracks for a specific album.
     * Spring Data Cassandra will automatically generate the implementation for this method.
     * @param albumId the ID of the album
     * @return a list of tracks
     */
    List<Track> findByAlbumId(UUID albumId);
}
