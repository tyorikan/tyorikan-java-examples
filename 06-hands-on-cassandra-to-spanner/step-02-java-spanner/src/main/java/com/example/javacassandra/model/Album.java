package com.example.javacassandra.model;

import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an album. This table is interleaved in the `singers` table.
 * Its primary key is composed of the parent's key (singerId) and its own unique key (albumId).
 */
@Table(name = "albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    @PrimaryKey(keyOrder = 1)
    private String singerId;

    @PrimaryKey(keyOrder = 2)
    private String albumId;

    private String title;

    private long releaseYear;

    @Interleaved
    private List<Track> tracks;
}