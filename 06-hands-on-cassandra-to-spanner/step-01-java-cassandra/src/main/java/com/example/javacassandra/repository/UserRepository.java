package com.example.javacassandra.repository;

import com.example.javacassandra.model.User;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {
}
