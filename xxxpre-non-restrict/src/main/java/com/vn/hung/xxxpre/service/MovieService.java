package com.vn.hung.xxxpre.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.vn.hung.xxxpre.dto.MovieDetailDto;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import com.vn.hung.xxxpre.entity.Movie;
import com.vn.hung.xxxpre.repository.MovieRepository;
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
    public PaginatedMovieResponse listMovies(String page, int size, String sortDirection) {
        // 1. Decode pageToken (if present)
        Map<String, AttributeValue> startKey = null;
        if (page != null && !page.isEmpty()) {
            try {
                startKey = decodePageToken(page);
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid page token", e);
            }
        }

        // 2. Determine Sort Order (ASC or DESC)
        boolean forward = "ASC".equalsIgnoreCase(sortDirection);

        // 3. Call Repository
        List<Movie> moviePage = movieRepository.scan(Movie.class);

        // 4. Encode next page token
        String nextPageToken = null;
//        if (moviePage.lastEvaluatedKey() != null && !moviePage.lastEvaluatedKey().isEmpty()) {
//            try {
//                nextPageToken = encodePageToken(moviePage.lastEvaluatedKey());
//            } catch (IOException e) {
//                // Handle error
//            }
//        }

        return new PaginatedMovieResponse(moviePage, nextPageToken);
    }

    // Helper: Encode Map -> Base64
    private String encodePageToken(Map<String, AttributeValue> lastKey) throws IOException {
        String jsonMap = objectMapper.writeValueAsString(lastKey);
        return Base64.getEncoder().encodeToString(jsonMap.getBytes());
    }

    // Helper: Decode Base64 -> Map
    private Map<String, AttributeValue> decodePageToken(String pageToken) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(pageToken);
        String jsonMap = new String(decodedBytes);
        return objectMapper.readValue(jsonMap, new TypeReference<>() {
        });
    }


    /**
     * Gets detailed metadata for a single video file.
     *
     * @param fileId The ID of the Google Drive file.
     * @return A DTO with detailed video information.
     */
    public MovieDetailDto getMovieDetail(String fileId) {
        try {
            // Request specific fields for the detail view
            String fields = "id, name, description, thumbnailLink, webViewLink, createdTime, videoMediaMetadata(durationMillis, width, height)";

            File file = driveService.files().get(fileId)
                    .setKey(apiKey)
                    .setFields(fields)
                    .execute();

            return MovieDetailDto.fromGoogleFile(file);

        } catch (IOException e) {
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