package com.vn.hung.xxxpre.controller;

import com.vn.hung.xxxpre.dto.MovieDetailDto;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import com.vn.hung.xxxpre.entity.Movie;
import com.vn.hung.xxxpre.service.MovieService; // Import new service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.data.web.PageableDefault; // Import for default settings
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    @Autowired
    private MovieService movieService; // Autowire the new service

    /**
     * Lists movies from the database with pagination.
     *
     * @param pageable Spring Boot automatically creates this from request params
     * (e.g., /api/v1/movies?page=0&size=20)
     * @return A PaginatedMovieResponse object.
     */
    @GetMapping
    public ResponseEntity<PaginatedMovieResponse> listMovies(
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "DESC") String sort) {

        return ResponseEntity.ok(movieService.listMovies(page, size, sort));
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