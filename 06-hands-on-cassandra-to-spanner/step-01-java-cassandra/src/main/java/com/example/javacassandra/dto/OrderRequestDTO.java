package com.example.javacassandra.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class OrderRequestDTO {
    private UUID userId;
    private UUID albumId;
}
