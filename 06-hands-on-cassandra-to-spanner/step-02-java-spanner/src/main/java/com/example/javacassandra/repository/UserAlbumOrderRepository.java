package com.example.javacassandra.repository;

import com.example.javacassandra.model.UserAlbumOrder;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAlbumOrderRepository extends SpannerRepository<UserAlbumOrder, String> {

    List<UserAlbumOrder> findByUserId(String userId);

    List<UserAlbumOrder> findBySingerIdAndAlbumId(String singerId, String albumId);
}
