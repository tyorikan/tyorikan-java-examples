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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test for the MusicService with a real Spanner database.
 * IMPORTANT: For this test to run, you must:
 * 1. Have a running Spanner instance and database.
 * 2. Fill in the details in `src/main/resources/application.properties`.
 * 3. Be authenticated with GCP (e.g., by running `gcloud auth application-default login`).
 */
@SpringBootTest
class MusicServiceSpannerIntegrationTest {

    @Autowired
    private MusicService musicService;

    @Test
    void shouldCreateAndRetrieveDiscography() {
        // 1. Create and save a singer
        Singer singer = new Singer();
        singer.setFirstName("Elena");
        singer.setLastName("Rodriguez");
        Singer savedSinger = musicService.addSinger(singer);
        assertNotNull(savedSinger.getSingerId());

        // 2. Create and save albums for the singer
        Album album1 = new Album();
        album1.setTitle("Echoes of Silence");
        album1.setReleaseYear(2022);
        Album savedAlbum1 = musicService.addAlbum(savedSinger.getSingerId(), album1);

        Album album2 = new Album();
        album2.setTitle("City Lights");
        album2.setReleaseYear(2024);
        Album savedAlbum2 = musicService.addAlbum(savedSinger.getSingerId(), album2);

        // 3. Create and save tracks for the albums
        Track track1_1 = new Track();
        track1_1.setTrackNumber(1);
        track1_1.setTitle("Fading Memories");
        musicService.addTrack(savedSinger.getSingerId(), savedAlbum1.getAlbumId(), track1_1);

        Track track1_2 = new Track();
        track1_2.setTrackNumber(2);
        track1_2.setTitle("Midnight Train");
        musicService.addTrack(savedSinger.getSingerId(), savedAlbum1.getAlbumId(), track1_2);

        Track track2_1 = new Track();
        track2_1.setTrackNumber(1);
        track2_1.setTitle("Neon Dreams");
        musicService.addTrack(savedSinger.getSingerId(), savedAlbum2.getAlbumId(), track2_1);

        // 4. Retrieve the full discography
        SingerDiscographyDTO discography = musicService.getSingerDiscography(savedSinger.getSingerId());

        // 5. Assert the results
        assertNotNull(discography);
        assertEquals("Elena", discography.getSinger().getFirstName());
        assertEquals(2, discography.getAlbums().size());

        // Find and verify the first album
        SingerDiscographyDTO.AlbumWithTracksDTO firstAlbumDto = discography.getAlbums().stream()
                .filter(a -> a.getAlbum().getTitle().equals("Echoes of Silence"))
                .findFirst().orElse(null);
        assertNotNull(firstAlbumDto);
        assertEquals(2, firstAlbumDto.getTracks().size());

        // Find and verify the second album
        SingerDiscographyDTO.AlbumWithTracksDTO secondAlbumDto = discography.getAlbums().stream()
                .filter(a -> a.getAlbum().getTitle().equals("City Lights"))
                .findFirst().orElse(null);
        assertNotNull(secondAlbumDto);
        assertEquals(1, secondAlbumDto.getTracks().size());
    }

    @Test
    void shouldCreateAndRetrieveUserOrders() {
        // 1. Create a user
        User user = new User();
        user.setName("Test User");
        User savedUser = musicService.addUser(user);
        assertNotNull(savedUser.getUserId());

        // 2. Create a singer and an album
        Singer singer = new Singer();
        singer.setFirstName("Order");
        singer.setLastName("Artist");
        Singer savedSinger = musicService.addSinger(singer);

        Album album = new Album();
        album.setTitle("The Purchasable");
        album.setReleaseYear(2025);
        Album savedAlbum = musicService.addAlbum(savedSinger.getSingerId(), album);
        assertNotNull(savedAlbum.getAlbumId());

        // 3. Place an order
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setUserId(savedUser.getUserId());
        orderRequest.setSingerId(savedSinger.getSingerId());
        orderRequest.setAlbumId(savedAlbum.getAlbumId());
        OrderDTO savedOrder = musicService.placeOrder(orderRequest);
        assertNotNull(savedOrder.getOrderId());

        // 4. Get orders for the user
        List<OrderDTO> userOrders = musicService.getOrdersForUser(savedUser.getUserId());
        assertEquals(1, userOrders.size());
        assertEquals(savedOrder.getOrderId(), userOrders.get(0).getOrderId());
        assertEquals(savedAlbum.getTitle(), userOrders.get(0).getAlbum().getTitle());

        // 5. Get users for the album
        AlbumWithUsersDTO albumWithUsers = musicService.getUsersForAlbum(savedSinger.getSingerId(), savedAlbum.getAlbumId());
        assertEquals(1, albumWithUsers.getUsers().size());
        assertEquals(savedUser.getName(), albumWithUsers.getUsers().get(0).getName());
    }
}