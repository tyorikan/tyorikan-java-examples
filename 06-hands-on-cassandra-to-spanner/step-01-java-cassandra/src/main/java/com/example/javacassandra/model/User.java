package com.example.javacassandra.model;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @PrimaryKey
    private UUID id;

    private String name;
}
