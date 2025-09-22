package com.example.javacassandra.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("orders_by_album")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdersByAlbum {

    @PrimaryKeyClass
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key {
        @PrimaryKeyColumn(name = "album_id", type = PrimaryKeyType.PARTITIONED)
        private UUID albumId;

        @PrimaryKeyColumn(name = "order_timestamp", ordinal = 0)
        private Instant orderTimestamp;

        @PrimaryKeyColumn(name = "order_id", ordinal = 1)
        private UUID orderId;
    }

    @PrimaryKey
    private Key key;

    private UUID userId;
}
