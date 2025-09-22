package com.example.javacassandra.dto;

import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private UUID orderId;
    private User user;
    private Album album;
    private Instant orderTimestamp;
}
