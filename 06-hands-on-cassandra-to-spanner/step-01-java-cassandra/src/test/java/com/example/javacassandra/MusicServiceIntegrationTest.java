package com.example.javacassandra;

import com.example.javacassandra.dto.AlbumWithUsersDTO;
import com.example.javacassandra.dto.OrderDTO;
import com.example.javacassandra.dto.OrderRequestDTO;
import com.example.javacassandra.dto.SingerDiscographyDTO;
import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.Singer;
import com.example.javacassandra.model.Track;
import com.example.javacassandra.model.User;
import com.example.javacassandra.service.MusicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class MusicServiceIntegrationTest {

    @Container
    private static final CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:latest");

    @Autowired
    private MusicService musicService;

    @DynamicPropertySource
    static void setCassandraProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", () -> cassandra.getContactPoint().getHostString());
        registry.add("spring.cassandra.port", () -> cassandra.getContactPoint().getPort());
        registry.add("spring.cassandra.keyspace-name", () -> "music_keyspace");
        registry.add("spring.cassandra.schema-action", () -> "CREATE_IF_NOT_EXISTS");
        registry.add("spring.cassandra.local-datacenter", cassandra::getLocalDatacenter);
    }

    @Test
    void testGetSingerDiscography_Success() {
        // Given a new singer
        Singer singer = new Singer();
        singer.setFirstName("John");
        singer.setLastName("Doe");

        // When the singer is added
        Singer savedSinger = musicService.addSinger(singer);
        assertNotNull(savedSinger.getId());

        // And an album is added for the singer
        Album album = new Album();
        album.setSingerId(savedSinger.getId());
        album.setTitle("Test Album");
        album.setReleaseYear(2025);
        Album savedAlbum = musicService.addAlbum(album);
        assertNotNull(savedAlbum.getId());

        // And a track is added to the album
        Track track = new Track();
        track.setAlbumId(savedAlbum.getId());
        track.setTrackNumber(1);
        track.setTitle("Test Track");
        Track savedTrack = musicService.addTrack(track);
        assertNotNull(savedTrack.getId());

        // Then the singer's discography can be retrieved
        SingerDiscographyDTO discography = musicService.getSingerDiscography(savedSinger.getId());

        // And the discography should be correct
        assertNotNull(discography);
        assertEquals(savedSinger.getFirstName(), discography.getSinger().getFirstName());
        assertEquals(1, discography.getAlbums().size());
        assertEquals(savedAlbum.getTitle(), discography.getAlbums().get(0).getAlbum().getTitle());
        assertEquals(1, discography.getAlbums().get(0).getTracks().size());
        assertEquals(savedTrack.getTitle(), discography.getAlbums().get(0).getTracks().get(0).getTitle());
    }

    @Test
    void testGetSingerDiscography_SingerNotFound() {
        // Given a non-existent singer ID
        UUID nonExistentSingerId = UUID.randomUUID();

        // When trying to get the discography
        // Then it should throw a RuntimeException
        assertThrows(RuntimeException.class, () -> {
            musicService.getSingerDiscography(nonExistentSingerId);
        });
    }

    @Test
    void testUserAndOrderLifecycle() {
        // 1. Create a user
        User user = new User();
        user.setName("Test User");
        User savedUser = musicService.addUser(user);
        assertNotNull(savedUser.getId());

        // 2. Create a singer and an album
        Singer singer = new Singer();
        singer.setFirstName("Album");
        singer.setLastName("Artist");
        Singer savedSinger = musicService.addSinger(singer);

        Album album = new Album();
        album.setSingerId(savedSinger.getId());
        album.setTitle("Purchasable Album");
        album.setReleaseYear(2024);
        Album savedAlbum = musicService.addAlbum(album);
        assertNotNull(savedAlbum.getId());

        // 3. User purchases the album
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setUserId(savedUser.getId());
        orderRequest.setAlbumId(savedAlbum.getId());
        OrderDTO placedOrder = musicService.placeOrder(orderRequest);
        assertNotNull(placedOrder.getOrderId());
        assertEquals(savedUser.getId(), placedOrder.getUser().getId());
        assertEquals(savedAlbum.getId(), placedOrder.getAlbum().getId());

        // 4. Get orders for the user and verify
        List<OrderDTO> userOrders = musicService.getOrdersForUser(savedUser.getId());
        assertEquals(1, userOrders.size());
        assertEquals(placedOrder.getOrderId(), userOrders.get(0).getOrderId());
        assertEquals(savedAlbum.getTitle(), userOrders.get(0).getAlbum().getTitle());

        // 5. Get users who purchased the album and verify
        AlbumWithUsersDTO albumWithUsers = musicService.getUsersForAlbum(savedAlbum.getId());
        assertEquals(1, albumWithUsers.getUsers().size());
        assertEquals(savedUser.getName(), albumWithUsers.getUsers().get(0).getName());
    }
}
