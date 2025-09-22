package com.example.javacassandra.dto;

import lombok.Data;

@Data
public class OrderRequestDTO {
    private String userId;
    private String singerId;
    private String albumId;
}
