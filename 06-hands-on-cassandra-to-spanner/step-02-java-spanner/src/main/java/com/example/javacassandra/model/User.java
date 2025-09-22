package com.example.javacassandra.model;

import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @PrimaryKey
    private String userId;

    private String name;
}
