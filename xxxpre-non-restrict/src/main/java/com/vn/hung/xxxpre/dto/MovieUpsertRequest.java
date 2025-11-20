package com.vn.hung.xxxpre.dto;

import java.util.List;

public class MovieUpsertRequest {
    // Movie Basic Info
    private String title;
    private String releaseDate;
    private String driveFileId;
    private String thumbnailLink;
    private String movieType = "Movie";

    // Movie Detail Info
    private String description;
    private Double rating;
    private String trailerUrl;
    private String webViewLink;
    private List<String> screenshots;
    private Long durationMillis;
    private Integer width;
    private Integer height;

    // Relationships (Can send ID or Name to create new)
    private List<CategoryRequest> categories;
    private List<ActorRequest> actors;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public String getDriveFileId() { return driveFileId; }
    public void setDriveFileId(String driveFileId) { this.driveFileId = driveFileId; }
    public String getThumbnailLink() { return thumbnailLink; }
    public void setThumbnailLink(String thumbnailLink) { this.thumbnailLink = thumbnailLink; }
    public String getMovieType() { return movieType; }
    public void setMovieType(String movieType) { this.movieType = movieType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public String getWebViewLink() { return webViewLink; }
    public void setWebViewLink(String webViewLink) { this.webViewLink = webViewLink; }
    public List<String> getScreenshots() { return screenshots; }
    public void setScreenshots(List<String> screenshots) { this.screenshots = screenshots; }
    public Long getDurationMillis() { return durationMillis; }
    public void setDurationMillis(Long durationMillis) { this.durationMillis = durationMillis; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public List<CategoryRequest> getCategories() { return categories; }
    public void setCategories(List<CategoryRequest> categories) { this.categories = categories; }
    public List<ActorRequest> getActors() { return actors; }
    public void setActors(List<ActorRequest> actors) { this.actors = actors; }

    public static class CategoryRequest {
        private String id;
        private String name; // Used if ID is null
        // getters/setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class ActorRequest {
        private String id;
        private String name; // Used if ID is null
        private String avatarLink;
        // getters/setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAvatarLink() { return avatarLink; }
        public void setAvatarLink(String avatarLink) { this.avatarLink = avatarLink; }
    }
}