package com.example.javacassandra.service;

import com.example.javacassandra.dto.AlbumWithUsersDTO;
import com.example.javacassandra.dto.OrderDTO;
import com.example.javacassandra.dto.OrderRequestDTO;
import com.example.javacassandra.dto.SingerDiscographyDTO;
import com.example.javacassandra.dto.SingerDiscographyDTO.AlbumWithTracksDTO;
import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.Singer;
import com.example.javacassandra.model.Track;
import com.example.javacassandra.model.User;
import com.example.javacassandra.model.UserAlbumOrder;
import com.example.javacassandra.repository.AlbumRepository;
import com.example.javacassandra.repository.SingerRepository;
import com.example.javacassandra.repository.TrackRepository;
import com.example.javacassandra.repository.UserAlbumOrderRepository;
import com.example.javacassandra.repository.UserRepository;
import com.google.cloud.spanner.Key;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final SingerRepository singerRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final UserAlbumOrderRepository userAlbumOrderRepository;

    @Transactional
    public Singer addSinger(Singer singer) {
        singer.setSingerId(UUID.randomUUID().toString());
        return singerRepository.save(singer);
    }

    @Transactional
    public Album addAlbum(String singerId, Album album) {
        album.setSingerId(singerId);
        album.setAlbumId(UUID.randomUUID().toString());
        return albumRepository.save(album);
    }

    @Transactional
    public Track addTrack(String singerId, String albumId, Track track) {
        track.setSingerId(singerId);
        track.setAlbumId(albumId);
        track.setTrackId(UUID.randomUUID().toString());
        return trackRepository.save(track);
    }

    @Transactional
    public User addUser(User user) {
        user.setUserId(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    /**
     * Retrieves the full discography for a given singer.
     * With Spanner's interleaved tables and Spring Data Spanner's entity mapping,
     * this operation becomes a single database call.
     * The `findById` call fetches the Singer and all its descendant rows (Albums and Tracks).
     * @param singerId The ID of the singer.
     * @return A DTO containing the singer and their complete discography.
     */
    @Transactional(readOnly = true)
    public SingerDiscographyDTO getSingerDiscography(String singerId) {
        // A single call to the database retrieves the singer and all their interleaved children.
        Singer singer = singerRepository.findById(singerId)
                .orElseThrow(() -> new RuntimeException("Singer not found with id: " + singerId));

        // Map the entity graph to the DTO graph. No more database calls needed.
        List<AlbumWithTracksDTO> albumDtos = singer.getAlbums() != null ?
                singer.getAlbums().stream().map(album -> {
                    List<Track> tracks = album.getTracks() != null ? album.getTracks() : Collections.emptyList();
                    // Create a new Album object without the tracks to avoid circular references in DTO
                    Album albumOnly = new Album(album.getSingerId(), album.getAlbumId(), album.getTitle(), album.getReleaseYear(), null);
                    return new AlbumWithTracksDTO(albumOnly, tracks);
                }).collect(Collectors.toList()) : Collections.emptyList();

        // Create a new Singer object without the albums to avoid circular references in DTO
        Singer singerOnly = new Singer(singer.getSingerId(), singer.getFirstName(), singer.getLastName(), null);

        return new SingerDiscographyDTO(singerOnly, albumDtos);
    }

    @Transactional
    public OrderDTO placeOrder(OrderRequestDTO orderRequest) {
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + orderRequest.getUserId()));
        Album album = albumRepository.findById(Key.of(orderRequest.getSingerId(), orderRequest.getAlbumId()))
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + orderRequest.getAlbumId()));

        UserAlbumOrder order = new UserAlbumOrder(
                UUID.randomUUID().toString(),
                user.getUserId(),
                album.getSingerId(),
                album.getAlbumId(),
                Instant.now()
        );
        userAlbumOrderRepository.save(order);

        return new OrderDTO(order.getOrderId(), user, album, order.getOrderTimestamp());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<UserAlbumOrder> orders = userAlbumOrderRepository.findByUserId(userId);

        return orders.stream().map(order -> {
            Album album = albumRepository.findById(Key.of(order.getSingerId(), order.getAlbumId()))
                    .orElseThrow(() -> new RuntimeException("Album not found"));
            return new OrderDTO(order.getOrderId(), user, album, order.getOrderTimestamp());
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlbumWithUsersDTO getUsersForAlbum(String singerId, String albumId) {
        Album album = albumRepository.findById(Key.of(singerId, albumId))
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + albumId));

        List<UserAlbumOrder> orders = userAlbumOrderRepository.findBySingerIdAndAlbumId(singerId, albumId);

        List<User> users = orders.stream()
                .map(order -> userRepository.findById(order.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + order.getUserId())))
                .collect(Collectors.toList());

        return new AlbumWithUsersDTO(album, users);
    }
}
