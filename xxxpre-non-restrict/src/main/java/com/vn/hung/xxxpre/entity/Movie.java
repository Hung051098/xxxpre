package com.vn.hung.xxxpre.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    private String id;

    private String releaseDate;
    private String driveFileId;
    private String title;

    @Column(length = 500)
    private String thumbnailLink;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn // Indicates that the MovieDetail shares the same PK as Movie
    @JsonManagedReference // Parent side of the relationship
    private MovieDetail movieDetail;

    // Changed from List<Category> to List<MovieCategory>
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Parent side
    private List<MovieCategory> movieCategories = new ArrayList<>();

    // Changed from List<Actor> to List<MovieActor>
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Parent side
    private List<MovieActor> movieActors = new ArrayList<>();

    // --------------------------------
    public Movie() {}

    // Helper to set Detail
    public void setMovieDetail(MovieDetail movieDetail) {
        if (movieDetail == null) {
            if (this.movieDetail != null) {
                this.movieDetail.setMovie(null);
            }
        } else {
            movieDetail.setMovie(this);
        }
        this.movieDetail = movieDetail;
    }

    public void addCategory(Category category) {
        MovieCategory movieCategory = new MovieCategory(this, category);
        this.movieCategories.add(movieCategory);
    }

    public void addActor(Actor actor) {
        MovieActor movieActor = new MovieActor(this, actor);
        this.movieActors.add(movieActor);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getThumbnailLink() { return thumbnailLink; }
    public void setThumbnailLink(String thumbnailLink) { this.thumbnailLink = thumbnailLink; }

    public String getDriveFileId() { return driveFileId; }
    public void setDriveFileId(String driveFileId) { this.driveFileId = driveFileId; }

    public MovieDetail getMovieDetail() { return movieDetail; }

    public List<MovieCategory> getMovieCategories() { return movieCategories; }
    public void setMovieCategories(List<MovieCategory> movieCategories) { this.movieCategories = movieCategories; }

    public List<MovieActor> getMovieActors() { return movieActors; }
    public void setMovieActors(List<MovieActor> movieActors) { this.movieActors = movieActors; }

}