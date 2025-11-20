package com.vn.hung.xxxpre.controller;

import com.vn.hung.xxxpre.dto.MovieUpsertRequest;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import com.vn.hung.xxxpre.entity.Movie;
import com.vn.hung.xxxpre.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<PaginatedMovieResponse> listMovies(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "DESC") String sort,
            @RequestParam(value = "keyword", required = false) String keyword) {

        PaginatedMovieResponse response = movieService.listMovies(page, size, sort, keyword);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestPart("request") MovieUpsertRequest request,
                                             @RequestPart(value = "video", required = false) MultipartFile video) {
        if (video.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        Movie createdMovie = movieService.createMovieWithVideoAsync(request, video);
        return ResponseEntity.ok(createdMovie);
    }
    /**
     * Gets detailed information for a single movie.
     *
     * @param fileId The Google Drive file ID.
     * @return A MovieDetailDto with metadata.
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Movie> getMovieDetail(
            @PathVariable String fileId) {
        // This still uses the DriveApiService as per your original code.
        // You can update this later to fetch from your 'movie_details' collection.
        return ResponseEntity.ok(movieService.getMovieDetail(fileId));
    }


    /**
     * Streams a movie.
     * This endpoint now returns a ResponseEntity<Resource>. Spring MVC
     * automatically handles the streaming of the Resource to the client.
     */
    @GetMapping("/stream/{fileId}")
    public ResponseEntity<Resource> streamMovie(
            @PathVariable String fileId,
            @RequestHeader(value = "Range", required = false) String range) {

        // The service now returns the *exact* response (status, headers, and body)
        // that we want to send to the client. We just return it directly.
        return movieService.streamMovie(fileId, range);
    }
}