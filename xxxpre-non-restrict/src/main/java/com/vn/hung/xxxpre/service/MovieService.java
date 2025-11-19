package com.vn.hung.xxxpre.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.vn.hung.xxxpre.dto.MovieDetailDto;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import com.vn.hung.xxxpre.entity.Movie;
import com.vn.hung.xxxpre.entity.MovieDetail;
import com.vn.hung.xxxpre.repository.MovieRepository;
import com.vn.hung.xxxpre.repository.base.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    /**
     * Lists movies with pagination and sorting.
     */
    public PaginatedMovieResponse listMovies(int page, int size, String sortDirection) {
        Map<String, AttributeValue> startKey = null;

        // 1. Get Totals (New Logic)
        int totalResults = movieRepository.count(Movie.class);
        int totalPages = (int) Math.ceil((double) totalResults / size);

        // 2. Skip pages to find the correct startKey
        for (int i = 1; i < page; i++) {
            PageResult<Movie> result = movieRepository.scanPage(Movie.class, startKey, size);
            startKey = result.getLastEvaluatedKey();

            // If startKey is null, we've exceeded the available pages
            if (startKey == null) {
                return new PaginatedMovieResponse(Collections.emptyList(), page, totalResults, totalPages);
            }
        }

        // 3. Fetch data
        PageResult<Movie> moviePage = movieRepository.scanPage(Movie.class, startKey, size);
        List<Movie> movies = moviePage.getItems();

        // 4. Return full response
        return new PaginatedMovieResponse(movies, page, totalResults, totalPages);
    }

    /**
     * Gets detailed metadata for a single video file.
     *
     * @param fileId The ID of the Google Drive file.
     * @return A DTO with detailed video information.
     */
    public Movie getMovieDetail(String fileId) {
        try {
            return movieRepository.query(fileId, null, Movie.class);
        } catch (Exception e) {
            // You might want a more specific exception handling (e.g., 404 Not Found)
            throw new RuntimeException("Failed to fetch movie detail from Google Drive for fileId: " + fileId, e);
        }
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