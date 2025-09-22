package com.example.javacassandra.model;

import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a track on an album. This table is interleaved in the `albums` table.
 * Its primary key is composed of all its parent keys plus its own unique key.
 */
@Table(name = "tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {

    @PrimaryKey(keyOrder = 1)
    private String singerId;

    @PrimaryKey(keyOrder = 2)
    private String albumId;

    @PrimaryKey(keyOrder = 3)
    private String trackId;

    private String title;

    private long trackNumber;
}