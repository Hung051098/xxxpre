package com.vn.hung.xxxpre.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "movie_details")
public class MovieDetail {

    @Id
    private String id;

    // @MapsId means "use the ID from the 'movie' relationship as my ID"
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonBackReference // Child side
    private Movie movie;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String webViewLink;
    private Double rating;
    private String trailerUrl;

    @ElementCollection
    @CollectionTable(name = "movie_screenshots", joinColumns = @JoinColumn(name = "movie_detail_id"))
    @Column(name = "url")
    private List<String> screenshots;

    @Embedded
    private VideoMetadata videoMetadata;

    public MovieDetail() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWebViewLink() { return webViewLink; }
    public void setWebViewLink(String webViewLink) { this.webViewLink = webViewLink; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public List<String> getScreenshots() { return screenshots; }
    public void setScreenshots(List<String> screenshots) { this.screenshots = screenshots; }

    public VideoMetadata getVideoMetadata() { return videoMetadata; }
    public void setVideoMetadata(VideoMetadata videoMetadata) { this.videoMetadata = videoMetadata; }

    @Embeddable
    public static class VideoMetadata {
        private Long durationMillis;
        private Integer width;
        private Integer height;

        public VideoMetadata() {}
        public VideoMetadata(Long d, Integer w, Integer h) {
            this.durationMillis = d; this.width = w; this.height = h;
        }
        // getters/setters
        public Long getDurationMillis() { return durationMillis; }
        public void setDurationMillis(Long durationMillis) { this.durationMillis = durationMillis; }
        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
    }
}