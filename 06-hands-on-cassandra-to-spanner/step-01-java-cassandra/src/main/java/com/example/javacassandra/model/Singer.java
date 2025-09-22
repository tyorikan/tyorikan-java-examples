package com.example.javacassandra.model;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

/**
 * Represents a singer.
 */
@Table("singers")
@Data
public class Singer {

    @PrimaryKey
    private UUID id;

    private String firstName;

    private String lastName;
}
