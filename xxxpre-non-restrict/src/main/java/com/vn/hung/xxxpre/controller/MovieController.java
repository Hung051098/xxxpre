package com.vn.hung.xxxpre.controller;

import com.vn.hung.xxxpre.dto.MovieDto;
import com.vn.hung.xxxpre.service.DriveApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource; // Import this
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// No longer need StreamingResponseBody
// import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    @Autowired
    private DriveApiService driveApiService;

    @GetMapping
    public ResponseEntity<List<MovieDto>> listMovies() {
        return ResponseEntity.ok(driveApiService.listMovies());
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