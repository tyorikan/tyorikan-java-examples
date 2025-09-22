package com.example.javacassandra.repository;

import java.util.UUID;

import com.example.javacassandra.model.Singer;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Singer entities.
 */
@Repository
public interface SingerRepository extends CassandraRepository<Singer, UUID> {
}
