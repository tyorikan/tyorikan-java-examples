package com.example.javacassandra.model;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

/**
 * Represents an album by a singer.
 */
@Table("albums")
@Data
public class Album {

    @PrimaryKey
    private UUID id;

    @Indexed
    private UUID singerId;

    private String title;

    private int releaseYear;

}
