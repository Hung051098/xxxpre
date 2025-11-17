package com.vn.hung.xxxpre.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.vn.hung.xxxpre.dto.MovieDetailDto;
import com.vn.hung.xxxpre.dto.MovieDto;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriveApiService {

    @Autowired
    private Drive driveService;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.folder.id}")
    private String folderId;

    private static final int PAGE_SIZE = 10;

    public PaginatedMovieResponse listMovies(String pageToken) {
        try {
            // 1. Define the query logic
            String query = String.format("'%s' in parents and mimeType contains 'video/' and trashed = false", folderId);

            // 2. Fetch the current page of data
            Drive.Files.List request = driveService.files().list()
                    .setKey(apiKey)
                    .setQ(query)
                    .setFields("nextPageToken, files(id, name, thumbnailLink)")
                    .setPageSize(PAGE_SIZE); // 10 items per page

            if (pageToken != null && !pageToken.isEmpty()) {
                request.setPageToken(pageToken);
            }

            FileList result = request.execute();
            List<File> files = result.getFiles();

            List<MovieDto> movieDtos;
            if (files != null) {
                movieDtos = files.stream()
                        .map(f -> new MovieDto(f.getId(), f.getName(), f.getThumbnailLink()))
                        .collect(Collectors.toList());
            } else {
                movieDtos = Collections.emptyList();
            }

            return new PaginatedMovieResponse(movieDtos, result.getNextPageToken());

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch movies from Google Drive", e);
        }
    }

    /**
     * Gets detailed metadata for a single video file.
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