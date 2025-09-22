package com.example.javacassandra.model;

import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "UserAlbumOrders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAlbumOrder {

    @PrimaryKey
    private String orderId;

    private String userId;

    private String singerId;

    private String albumId;

    private Instant orderTimestamp;
}
