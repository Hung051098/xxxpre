package com.vn.hung.xxxpre.controller;

import com.vn.hung.xxxpre.dto.MovieDetailDto;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import com.vn.hung.xxxpre.service.DriveApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    @Autowired
    private DriveApiService driveApiService;

    /**
     * Lists movies with pagination.
     *
     * @param pageToken The token for the next page, from the previous response.
     *                  (e.g., /api/v1/movies?page=...token...)
     * @return A PaginatedMovieResponse object.
     */
    @GetMapping
    public ResponseEntity<PaginatedMovieResponse> listMovies(
            @RequestParam(value = "page", required = false) String pageToken) {

        return ResponseEntity.ok(driveApiService.listMovies(pageToken));
    }


    /**
     * Gets detailed information for a single movie.
     *
     * @param fileId The Google Drive file ID.
     * @return A MovieDetailDto with metadata.
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<MovieDetailDto> getMovieDetail(
            @PathVariable String fileId) {
        return ResponseEntity.ok(driveApiService.getMovieDetail(fileId));
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
        return driveApiService.streamMovie(fileId, range);
    }
}