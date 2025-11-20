package com.vn.hung.xxxpre.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.vn.hung.xxxpre.dto.MovieUpsertRequest;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import com.vn.hung.xxxpre.entity.Actor;
import com.vn.hung.xxxpre.entity.Category;
import com.vn.hung.xxxpre.entity.Movie;
import com.vn.hung.xxxpre.entity.MovieDetail;
import com.vn.hung.xxxpre.repository.ActorRepository;
import com.vn.hung.xxxpre.repository.CategoryRepository;
import com.vn.hung.xxxpre.repository.MovieDetailRepository;
import com.vn.hung.xxxpre.repository.MovieRepository;
import com.vn.hung.xxxpre.service.MovieService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.UUID;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private Drive driveService;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.folder.id}")
    private String folderId;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MovieDetailRepository movieDetailRepository;

    /**
     * Lists movies with pagination, sorting by releaseDate, and keyword search.
     *
     * @param page          Current page number (1-based from controller)
     * @param size          Page size
     * @param sortDirection "ASC" or "DESC"
     * @param keyword       Search term for Title, Category, or Actor
     */
    @Override
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

    @Transactional
    public Movie createMovieFull(MovieUpsertRequest request) {
        // 1. Create Parent Movie Entity
        Movie movie = new Movie();
        // Generate ID manually or let JPA do it (here we do manual UUID as per your code style)
        String movieId = UUID.randomUUID().toString();
        movie.setId(movieId);

        movie.setTitle(request.getTitle());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setThumbnailLink(request.getThumbnailLink());

        // 2. Create and Link MovieDetail (One-to-One)
        MovieDetail detail = new MovieDetail();
        detail.setDescription(request.getDescription());
        detail.setRating(request.getRating());
        detail.setTrailerUrl(request.getTrailerUrl());
        detail.setWebViewLink(request.getWebViewLink());
        detail.setScreenshots(request.getScreenshots());

        if (request.getDurationMillis() != null) {
            detail.setVideoMetadata(new MovieDetail.VideoMetadata(
                    request.getDurationMillis(), request.getWidth(), request.getHeight()));
        }

        // Link them!
        movie.setMovieDetail(detail);

        // 3. Handle Categories (Same logic: find or create)
        if (request.getCategories() != null) {
            for (MovieUpsertRequest.CategoryRequest catReq : request.getCategories()) {
                Category category = null;
                if (catReq.getId() != null) {
                    category = categoryRepository.findById(catReq.getId()).orElse(null);
                }
                if (category == null && catReq.getName() != null) {
                    category = categoryRepository.findByName(catReq.getName())
                            .orElseGet(() -> {
                                Category newCat = new Category();
                                newCat.setId(UUID.randomUUID().toString());
                                newCat.setName(catReq.getName());
                                newCat.setSlug(catReq.getName().toLowerCase().replace(" ", "-"));
                                return categoryRepository.save(newCat);
                            });
                }
                if (category != null) {
                    movie.addCategory(category);
                }
            }
        }

        // 4. Handle Actors (Same logic: find or create)
        if (request.getActors() != null) {
            for (MovieUpsertRequest.ActorRequest actorReq : request.getActors()) {
                Actor actor = null;
                if (actorReq.getId() != null) {
                    actor = actorRepository.findById(actorReq.getId()).orElse(null);
                }
                if (actor == null && actorReq.getName() != null) {
                    actor = actorRepository.findByName(actorReq.getName())
                            .orElseGet(() -> {
                                Actor newActor = new Actor();
                                newActor.setId(UUID.randomUUID().toString());
                                newActor.setName(actorReq.getName());
                                newActor.setAvatarLink(actorReq.getAvatarLink());
                                return actorRepository.save(newActor);
                            });
                }
                if (actor != null) {
                    movie.addActor(actor);
                }
            }
        }

        // 5. Single Save!
        // Cascades will save Movie -> MovieDetail, MovieCategory, MovieActor
        return movieRepository.save(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public Movie getMovieDetail(String fileId) {
        return movieRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + fileId));
    }


    @Override
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

    public Movie createMovieWithVideoAsync(MovieUpsertRequest request, MultipartFile videoFile) {
        // 1. Save Movie to Database (Initial Save)
        // Reuse your existing logic. The driveFileId might be null initially.
        Movie savedMovie = createMovieFull(request);

        // 2. Handle Video Upload Asynchronously
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                // A. Copy MultipartFile to a temporary local file.
                // We MUST do this because the MultipartFile stream closes when the main request ends.
                File tempFile = File.createTempFile("upload_temp_", "_" + videoFile.getOriginalFilename());
                videoFile.transferTo(tempFile);
                String mimeType = videoFile.getContentType();

                // B. Open a NEW THREAD
                new Thread(() -> {
                    try {
                        System.out.println("Thread started: Uploading video for movie " + savedMovie.getId());

                        // C. Upload to Google Drive
                        String driveId = uploadLocalFileToDrive(tempFile, mimeType);

                        // D. Update Database with new Drive ID
                        savedMovie.setDriveFileId(driveId);
                        movieRepository.save(savedMovie);

                        System.out.println("Upload success. Updated Movie ID: " + savedMovie.getId());

                    } catch (Exception e) {
                        System.err.println("Background upload failed: " + e.getMessage());
                        e.printStackTrace();
                        // Optional: Update DB with an "Error" status if you have such a field
                    } finally {
                        // E. Cleanup Temp File
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                    }
                }).start();

            } catch (IOException e) {
                throw new RuntimeException("Failed to prepare video file for background upload", e);
            }
        }

        // Return the movie object (Video upload is still pending/in-progress)
        return savedMovie;
    }

    /**
     * Helper to upload a java.io.File to Google Drive
     */
    private String uploadLocalFileToDrive(File file, String mimeType) throws IOException {
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(file.getName()); // Or use original filename if passed

        if (folderId != null && !folderId.isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }

        // Use FileContent for java.io.File
        FileContent mediaContent = new FileContent(mimeType, file);

        com.google.api.services.drive.model.File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return uploadedFile.getId();
    }
}