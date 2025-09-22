package com.example.javacassandra.dto;

import java.util.List;

import com.example.javacassandra.model.Album;
import com.example.javacassandra.model.Singer;
import com.example.javacassandra.model.Track;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the full discography for a singer, including all albums and their tracks.
 * This DTO is used to assemble data from multiple tables (Singers, Albums, Tracks).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingerDiscographyDTO {

    private Singer singer;
    private List<AlbumWithTracksDTO> albums;

    /**
     * Inner DTO to hold an album and its list of tracks.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlbumWithTracksDTO {
        private Album album;
        private List<Track> tracks;
    }
}
