package com.vn.hung.xxxpre.controller;

import com.vn.hung.xxxpre.dto.MovieDto;
import com.vn.hung.xxxpre.service.DriveApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/stream/{fileId}")
    public ResponseEntity<Byte> streamMovie(
            @PathVariable String fileId,
            @RequestHeader(value = "Range", required = false) String range) {

        // Call the service to proxy the request to Google Drive
        ResponseEntity<Byte> googleResponse = driveApiService.streamMovie(fileId, range);

        // Get the headers from Google's response
        HttpHeaders responseHeaders = googleResponse.getHeaders();

        // Create our own response, copying Google's status, headers, and body
        // This effectively turns our server into a proxy.
        // The browser will receive a 200 (full) or 206 (partial) from us,
        // exactly as Google sent it.
        return ResponseEntity
                .status(googleResponse.getStatusCode())
                .headers(responseHeaders)
                .body(googleResponse.getBody());
    }
}