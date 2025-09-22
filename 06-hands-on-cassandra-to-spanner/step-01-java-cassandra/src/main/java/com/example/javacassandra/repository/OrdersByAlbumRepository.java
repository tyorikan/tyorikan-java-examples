package com.example.javacassandra.repository;

import com.example.javacassandra.model.OrdersByAlbum;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersByAlbumRepository extends CassandraRepository<OrdersByAlbum, OrdersByAlbum.Key> {
    List<OrdersByAlbum> findByKeyAlbumId(UUID albumId);
}
