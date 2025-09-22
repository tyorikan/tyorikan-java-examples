package com.example.javacassandra.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.javacassandra.dto.AlbumWithUsersDTO;
import com.example.javacassandra.dto.OrderDTO;
import com.example.javacassandra.dto.OrderRequestDTO;
import com.example.javacassandra.dto.SingerDiscographyDTO;
import com.example.javacassandra.dto.SingerDiscographyDTO.AlbumWithTracksDTO;
import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.OrdersByAlbum;
import com.example.javacassandra.model.OrdersByUser;
import com.example.javacassandra.model.Singer;
import com.example.javacassandra.model.Track;
import com.example.javacassandra.model.User;
import com.example.javacassandra.repository.AlbumRepository;
import com.example.javacassandra.repository.OrdersByAlbumRepository;
import com.example.javacassandra.repository.OrdersByUserRepository;
import com.example.javacassandra.repository.SingerRepository;
import com.example.javacassandra.repository.TrackRepository;
import com.example.javacassandra.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final SingerRepository singerRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final OrdersByUserRepository ordersByUserRepository;
    private final OrdersByAlbumRepository ordersByAlbumRepository;

    public Singer addSinger(Singer singer) {
        singer.setId(UUID.randomUUID());
        return singerRepository.save(singer);
    }

    public Album addAlbum(Album album) {
        album.setId(UUID.randomUUID());
        return albumRepository.save(album);
    }

    public Track addTrack(Track track) {
        track.setId(UUID.randomUUID());
        return trackRepository.save(track);
    }

    public User addUser(User user) {
        user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }

    /**
     * Retrieves the full discography for a given singer.
     * This method demonstrates a common pattern where multiple database queries are needed
     * to assemble a complete data object.
     * 1. Fetch the singer.
     * 2. Fetch all albums for the singer.
     * 3. For each album, fetch all its tracks.
     * @param id The ID of the singer.
     * @return A DTO containing the singer and their complete discography.
     */
    public SingerDiscographyDTO getSingerDiscography(UUID id) {
        // 1. Fetch the singer
        Singer singer = singerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Singer not found with id: " + id));

        // 2. Fetch all albums for the singer
        List<Album> albums = albumRepository.findBySingerId(id);

        // 3. For each album, fetch its tracks and assemble the DTO
        List<AlbumWithTracksDTO> albumWithTracksList = albums.stream()
                .map(album -> {
                    List<Track> tracks = trackRepository.findByAlbumId(album.getId());
                    return new AlbumWithTracksDTO(album, tracks);
                })
                .collect(Collectors.toList());

        return new SingerDiscographyDTO(singer, albumWithTracksList);
    }

    public OrderDTO placeOrder(OrderRequestDTO orderRequest) {
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + orderRequest.getUserId()));
        Album album = albumRepository.findById(orderRequest.getAlbumId())
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + orderRequest.getAlbumId()));

        UUID orderId = UUID.randomUUID();
        Instant orderTimestamp = Instant.now();

        OrdersByUser.Key byUserKey = new OrdersByUser.Key(user.getId(), orderTimestamp, orderId);
        OrdersByUser ordersByUser = new OrdersByUser(byUserKey, album.getId());
        ordersByUserRepository.save(ordersByUser);

        OrdersByAlbum.Key byAlbumKey = new OrdersByAlbum.Key(album.getId(), orderTimestamp, orderId);
        OrdersByAlbum ordersByAlbum = new OrdersByAlbum(byAlbumKey, user.getId());
        ordersByAlbumRepository.save(ordersByAlbum);

        return new OrderDTO(orderId, user, album, orderTimestamp);
    }

    public List<OrderDTO> getOrdersForUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<OrdersByUser> orders = ordersByUserRepository.findByKeyUserId(userId);

        return orders.stream().map(order -> {
            Album album = albumRepository.findById(order.getAlbumId())
                    .orElseThrow(() -> new RuntimeException("Album not found with id: " + order.getAlbumId()));
            User user = userRepository.findById(order.getKey().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + order.getKey().getUserId()));
            return new OrderDTO(order.getKey().getOrderId(), user, album, order.getKey().getOrderTimestamp());
        }).collect(Collectors.toList());
    }

    public AlbumWithUsersDTO getUsersForAlbum(UUID albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + albumId));

        List<OrdersByAlbum> orders = ordersByAlbumRepository.findByKeyAlbumId(albumId);

        List<User> users = orders.stream()
                .map(order -> userRepository.findById(order.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + order.getUserId())))
                .collect(Collectors.toList());

        return new AlbumWithUsersDTO(album, users);
    }
}
