package com.vn.hung.xxxpre.dto;

import com.vn.hung.xxxpre.entity.Movie;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Collectors;

public class PaginatedMovieResponse {
    private List<Movie> movies;
    private String nextPageToken;
    private long totalResults;
    private int totalPages;

    // New constructor to map from Spring's Page object
    public PaginatedMovieResponse(Page<Movie> moviePage) {
        // Convert List<Movie> to List<Movie>
        this.movies = moviePage.getContent().stream()
                .map(Movie::new) // Uses the new constructor from Movie
                .collect(Collectors.toList());

        // Set the next page token (just the next page number, or null if it's the last page)
        this.nextPageToken = moviePage.hasNext() ? String.valueOf(moviePage.getNumber() + 1) : null;

        this.totalResults = moviePage.getTotalElements();
        this.totalPages = moviePage.getTotalPages();
    }

    // Original constructor (if needed)
    public PaginatedMovieResponse(List<Movie> movies, String nextPageToken) {
        this.movies = movies;
        this.nextPageToken = nextPageToken;
    }

    // Getters
    public List<Movie> getMovies() { return movies; }
    public String getNextPageToken() { return nextPageToken; }
    public long getTotalResults() { return totalResults; }
    public int getTotalPages() { return totalPages; }

    // Setters
    public void setMovies(List<Movie> movies) { this.movies = movies; }
    public void setNextPageToken(String nextPageToken) { this.nextPageToken = nextPageToken; }
    public void setTotalResults(long totalResults) { this.totalResults = totalResults; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}