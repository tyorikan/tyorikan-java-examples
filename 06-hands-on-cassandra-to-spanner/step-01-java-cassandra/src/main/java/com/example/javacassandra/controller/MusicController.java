package com.example.javacassandra.controller;

import java.util.List;
import java.util.UUID;

import com.example.javacassandra.dto.AlbumWithUsersDTO;
import com.example.javacassandra.dto.OrderDTO;
import com.example.javacassandra.dto.OrderRequestDTO;
import com.example.javacassandra.dto.SingerDiscographyDTO;
import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.Singer;
import com.example.javacassandra.model.Track;
import com.example.javacassandra.model.User;
import com.example.javacassandra.service.MusicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping("/singers")
    public ResponseEntity<Singer> addSinger(@RequestBody Singer singer) {
        return ResponseEntity.ok(musicService.addSinger(singer));
    }

    @PostMapping("/albums")
    public ResponseEntity<Album> addAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(musicService.addAlbum(album));
    }

    @PostMapping("/tracks")
    public ResponseEntity<Track> addTrack(@RequestBody Track track) {
        return ResponseEntity.ok(musicService.addTrack(track));
    }

    @GetMapping("/singers/{id}/discography")
    public ResponseEntity<SingerDiscographyDTO> getSingerDiscography(@PathVariable UUID id) {
        return ResponseEntity.ok(musicService.getSingerDiscography(id));
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return ResponseEntity.ok(musicService.addUser(user));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        return ResponseEntity.ok(musicService.placeOrder(orderRequest));
    }

    @GetMapping("/users/{id}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersForUser(@PathVariable UUID id) {
        return ResponseEntity.ok(musicService.getOrdersForUser(id));
    }

    @GetMapping("/albums/{id}/users")
    public ResponseEntity<AlbumWithUsersDTO> getUsersForAlbum(@PathVariable UUID id) {
        return ResponseEntity.ok(musicService.getUsersForAlbum(id));
    }
}
