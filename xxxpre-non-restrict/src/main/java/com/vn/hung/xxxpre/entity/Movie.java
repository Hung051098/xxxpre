package com.vn.hung.xxxpre.entity;

import com.vn.hung.xxxpre.repository.base.DynamoDbTableName;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import java.util.List;

@DynamoDbBean
@DynamoDbTableName("movies")
public class Movie {

    private String id;
    private String movieType = "Movie"; // <--- FIX 1: Initialize default value
    private String releaseDate;
    private String driveFileId;
    private String title;
    private String thumbnailLink;

    private List<String> categoryIds;
    private List<String> actorIds;
    private List<String> displayCategories;
    private List<String> displayActors;

    public Movie() {
        this.movieType = "Movie"; // <--- FIX 1: Ensure it's set in no-arg constructor
    }

    public Movie(String driveFileId, String title, String thumbnailLink) {
        this.driveFileId = driveFileId;
        this.title = title;
        this.thumbnailLink = thumbnailLink;
        this.movieType = "Movie";
    }

    // <--- FIX 2: Fix the broken Copy Constructor
    public Movie(Movie movie) {
        this.id = movie.getId();
        this.movieType = movie.getMovieType();
        this.releaseDate = movie.getReleaseDate();
        this.driveFileId = movie.getDriveFileId();
        this.title = movie.getTitle();
        this.thumbnailLink = movie.getThumbnailLink();
        this.categoryIds = movie.getCategoryIds();
        this.actorIds = movie.getActorIds();
        this.displayCategories = movie.getDisplayCategories();
        this.displayActors = movie.getDisplayActors();
    }

    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @DynamoDbSecondaryPartitionKey(indexNames = "movies-by-releaseDate-gsi")
    public String getMovieType() { return movieType; }
    public void setMovieType(String movieType) { this.movieType = movieType; }

    @DynamoDbSecondarySortKey(indexNames = "movies-by-releaseDate-gsi")
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getDriveFileId() { return driveFileId; }
    public void setDriveFileId(String driveFileId) { this.driveFileId = driveFileId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getThumbnailLink() { return thumbnailLink; }
    public void setThumbnailLink(String thumbnailLink) { this.thumbnailLink = thumbnailLink; }

    public List<String> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<String> categoryIds) { this.categoryIds = categoryIds; }

    public List<String> getActorIds() { return actorIds; }
    public void setActorIds(List<String> actorIds) { this.actorIds = actorIds; }

    public List<String> getDisplayCategories() { return displayCategories; }
    public void setDisplayCategories(List<String> displayCategories) { this.displayCategories = displayCategories; }

    public List<String> getDisplayActors() { return displayActors; }
    public void setDisplayActors(List<String> displayActors) { this.displayActors = displayActors; }
}