package com.example.javacassandra.dto;

import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumWithUsersDTO {
    private Album album;
    private List<User> users;
}
