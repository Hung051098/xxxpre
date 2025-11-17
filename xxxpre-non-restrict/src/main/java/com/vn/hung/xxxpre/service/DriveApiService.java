package com.vn.hung.xxxpre.service;

import com.vn.hung.xxxpre.dto.MovieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource; // Import this
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Service
public class DriveApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.folder.id}")
    private String folderId;

    private static final String DRIVE_FILES_URL = "https://www.googleapis.com/drive/v3/files";

    // Internal record to match the nested JSON structure from Google Drive API
    private record GoogleDriveFileResponse(List<MovieDto> files) {}

    public List<MovieDto> listMovies() {
        String queryString = String.format("'%s' in parents", folderId);
        String url = UriComponentsBuilder.fromHttpUrl(DRIVE_FILES_URL)
                .queryParam("key", apiKey)
                .queryParam("q", queryString)
                .queryParam("fields", "files(id, name, thumbnailLink)")
                .toUriString();
        GoogleDriveFileResponse response = restTemplate.getForObject(url, GoogleDriveFileResponse.class);
        return Objects.requireNonNull(response).files();
    }

    /**
     * Streams a movie file from Google Drive.
     * This method uses restTemplate.exchange() to request the body as a Resource.
     * Spring (with HttpComponents) will return an InputStreamResource that wraps
     * the live HTTP stream, which we can then pass directly to the client.
     *
     * @param fileId      The Google Drive file ID.
     * @param rangeHeader The "Range" header from the client's request.
     * @return A ResponseEntity containing the Resource (the video stream).
     */
    public ResponseEntity<Resource> streamMovie(String fileId, String rangeHeader) {
        // Build the Google Drive file download URL
        String url = UriComponentsBuilder.fromHttpUrl(DRIVE_FILES_URL + "/" + fileId)
                .queryParam("key", apiKey)
                .queryParam("alt", "media") // Request the file content
                .toUriString();

        // Create HttpHeaders and add the Range header if it exists
        HttpHeaders headers = new HttpHeaders();
        if (rangeHeader != null && !rangeHeader.isEmpty()) {
            headers.set("Range", rangeHeader); // Pass the client's Range header to Google
        }

        // Create an HttpEntity with the headers
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Execute the request and get the response as a ResponseEntity<Resource>
        // This is the key change. We are no longer using ResponseExtractor.
        // Spring MVC will handle streaming this Resource to the client.
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Resource.class // Ask for the body as a Resource
        );
    }
}