package com.example.javacassandra.dto;

import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.User;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String orderId;
    private User user;
    private Album album;
    private Instant orderTimestamp;
}
