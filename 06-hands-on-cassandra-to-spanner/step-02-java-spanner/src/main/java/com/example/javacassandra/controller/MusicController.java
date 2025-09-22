package com.example.javacassandra.controller;

import com.example.javacassandra.dto.AlbumWithUsersDTO;
import com.example.javacassandra.dto.OrderDTO;
import com.example.javacassandra.dto.OrderRequestDTO;
import com.example.javacassandra.dto.SingerDiscographyDTO;
import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.Singer;
import com.example.javacassandra.model.Track;
import com.example.javacassandra.model.User;
import com.example.javacassandra.service.MusicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping("/singers")
    public ResponseEntity<Singer> addSinger(@RequestBody Singer singer) {
        return ResponseEntity.ok(musicService.addSinger(singer));
    }

    @PostMapping("/singers/{singerId}/albums")
    public ResponseEntity<Album> addAlbum(
            @PathVariable String singerId,
            @RequestBody Album album) {
        return ResponseEntity.ok(musicService.addAlbum(singerId, album));
    }

    @PostMapping("/singers/{singerId}/albums/{albumId}/tracks")
    public ResponseEntity<Track> addTrack(
            @PathVariable String singerId,
            @PathVariable String albumId,
            @RequestBody Track track) {
        return ResponseEntity.ok(musicService.addTrack(singerId, albumId, track));
    }

    @GetMapping("/singers/{singerId}/discography")
    public ResponseEntity<SingerDiscographyDTO> getSingerDiscography(@PathVariable String singerId) {
        return ResponseEntity.ok(musicService.getSingerDiscography(singerId));
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return ResponseEntity.ok(musicService.addUser(user));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        return ResponseEntity.ok(musicService.placeOrder(orderRequest));
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersForUser(@PathVariable String userId) {
        return ResponseEntity.ok(musicService.getOrdersForUser(userId));
    }

    @GetMapping("/singers/{singerId}/albums/{albumId}/users")
    public ResponseEntity<AlbumWithUsersDTO> getUsersForAlbum(
            @PathVariable String singerId, @PathVariable String albumId) {
        return ResponseEntity.ok(musicService.getUsersForAlbum(singerId, albumId));
    }
}
