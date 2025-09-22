package com.example.javacassandra.repository;

import com.example.javacassandra.model.OrdersByUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersByUserRepository extends CassandraRepository<OrdersByUser, OrdersByUser.Key> {
    List<OrdersByUser> findByKeyUserId(UUID userId);
}
