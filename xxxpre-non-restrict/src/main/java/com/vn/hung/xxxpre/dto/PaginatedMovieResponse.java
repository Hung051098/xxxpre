package com.vn.hung.xxxpre.dto;

import com.vn.hung.xxxpre.entity.Movie;
import java.util.List;

public class PaginatedMovieResponse {
    private List<Movie> movies;
    private int currentPage;     // Changed from nextPageToken to currentPage
    private long totalResults;
    private int totalPages;

    public PaginatedMovieResponse(List<Movie> movies, int currentPage, long totalResults, int totalPages) {
        this.movies = movies;
        this.currentPage = currentPage;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    // Getters and Setters
    public List<Movie> getMovies() { return movies; }
    public void setMovies(List<Movie> movies) { this.movies = movies; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public long getTotalResults() { return totalResults; }
    public void setTotalResults(long totalResults) { this.totalResults = totalResults; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}