package com.example.javacassandra.repository;

import com.example.javacassandra.model.User;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends SpannerRepository<User, String> {
}
