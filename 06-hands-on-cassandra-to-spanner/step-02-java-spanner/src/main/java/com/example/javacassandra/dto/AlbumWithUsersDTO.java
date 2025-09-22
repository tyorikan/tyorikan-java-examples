package com.example.javacassandra.dto;

import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumWithUsersDTO {
    private Album album;
    private List<User> users;
}
