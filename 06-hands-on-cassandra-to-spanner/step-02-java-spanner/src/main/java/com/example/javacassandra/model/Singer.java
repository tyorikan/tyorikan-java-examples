package com.example.javacassandra.model;

import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a singer. This is the root entity for the interleaved hierarchy.
 */
@Table(name = "singers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Singer {

    @PrimaryKey
    private String singerId;

    private String firstName;

    private String lastName;

    @Interleaved
    private List<Album> albums;
}