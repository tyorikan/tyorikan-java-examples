package com.example.javacassandra.repository;

import java.util.List;
import java.util.UUID;

import com.example.javacassandra.model.Album;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Album entities.
 */
@Repository
public interface AlbumRepository extends CassandraRepository<Album, UUID> {

    /**
     * Finds all albums by a specific singer.
     * Spring Data Cassandra will automatically generate the implementation for this method.
     * @param singerId the ID of the singer
     * @return a list of albums
     */
    List<Album> findBySingerId(UUID singerId);
}
