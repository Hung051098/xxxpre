package com.vn.hung.xxxpre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.Drive;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import com.vn.hung.xxxpre.entity.Movie;
import com.vn.hung.xxxpre.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private Drive driveService;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.folder.id}")
    private String folderId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Lists movies with pagination, sorting by releaseDate, and keyword search.
     *
     * @param page          Current page number (1-based from controller)
     * @param size          Page size
     * @param sortDirection "ASC" or "DESC"
     * @param keyword       Search term for Title, Category, or Actor
     */
    public PaginatedMovieResponse listMovies(int page, int size, String sortDirection, String keyword) {
        // 1. Validate Page
        if (page < 1) page = 1;

        // 2. Create Sort (Default to DESC if invalid)
        Sort.Direction direction = Sort.Direction.DESC;
        if (sortDirection != null && sortDirection.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        }
        Sort sort = Sort.by(direction, "releaseDate");

        // 3. Create Pageable (Spring Data JPA uses 0-based indexing)
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // 4. Execute Query
        Page<Movie> moviePage = movieRepository.searchByKeyword(keyword, pageable);

        // 5. Build Response
        if (moviePage.isEmpty()) {
            return new PaginatedMovieResponse(Collections.emptyList(), page, 0, 0);
        }

        return new PaginatedMovieResponse(
                moviePage.getContent(),
                page,
                moviePage.getTotalElements(),
                moviePage.getTotalPages()
        );
    }

    public Movie getMovieDetail(String fileId) {
        return movieRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + fileId));
    }


    public ResponseEntity<Resource> streamMovie(String fileId, String rangeHeader) {
        try {
            Drive.Files.Get getRequest = driveService.files().get(fileId);
            getRequest.setKey(apiKey);
            getRequest.getMediaHttpDownloader().setDirectDownloadEnabled(true);

            if (rangeHeader != null && !rangeHeader.isEmpty()) {
                getRequest.getRequestHeaders().setRange(rangeHeader);
            }

            var response = getRequest.executeMedia();

            InputStream inputStream = response.getContent();
            InputStreamResource resource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            if (response.getHeaders().getContentType() != null) {
                headers.setContentType(MediaType.parseMediaType(response.getHeaders().getContentType()));
            }
            if (response.getHeaders().getContentLength() != null) {
                headers.setContentLength(response.getHeaders().getContentLength());
            }
            if (response.getHeaders().getContentRange() != null) {
                headers.set("Content-Range", response.getHeaders().getContentRange());
                return ResponseEntity.status(206).headers(headers).body(resource);
            }

            return ResponseEntity.ok().headers(headers).body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Failed to stream movie from Google Drive", e);
        }
    }
}