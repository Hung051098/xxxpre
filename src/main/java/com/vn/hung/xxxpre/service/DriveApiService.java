package com.vn.hung.xxxpre.service;

import com.vn.hung.xxxpre.dto.MovieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        // Build the query string: "'{folderId}' in parents"
        String queryString = String.format("'%s' in parents", folderId);

        // Build the URL with query parameters
        String url = UriComponentsBuilder.fromHttpUrl(DRIVE_FILES_URL)
                .queryParam("key", apiKey)
                .queryParam("q", queryString)
                .queryParam("fields", "files(id, name, thumbnailLink)") // Request only these fields
                .toUriString();

        // Make the API call and map the response
        GoogleDriveFileResponse response = restTemplate.getForObject(url, GoogleDriveFileResponse.class);

        return Objects.requireNonNull(response).files();
    }

    public ResponseEntity<Byte> streamMovie(String fileId, String rangeHeader) {
        // Build the Google Drive file download URL
        String url = UriComponentsBuilder.fromHttpUrl(DRIVE_FILES_URL + "/" + fileId)
                .queryParam("key", apiKey)
                .queryParam("alt", "media") // Request the file content
                .toUriString();

        // Create HttpHeaders and add the Range header if it exists
        HttpHeaders headers = new HttpHeaders();
        if (rangeHeader!= null &&!rangeHeader.isEmpty()) {
            headers.set("Range", rangeHeader); // Pass the client's Range header to Google
        }

        // Create an HttpEntity with the headers
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Execute the request using RestTemplate.exchange()
        // This allows us to get the full ResponseEntity, including status and headers
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte.class
        );
    }
}
