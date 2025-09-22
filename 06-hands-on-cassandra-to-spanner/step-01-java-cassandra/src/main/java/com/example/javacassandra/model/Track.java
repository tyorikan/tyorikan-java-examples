package com.example.javacassandra.model;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

/**
 * Represents a track on an album.
 */
@Table("tracks")
@Data
public class Track {

    @PrimaryKey
    private UUID id;

    @Indexed
    private UUID albumId;

    private int trackNumber;

    private String title;

}
