package com.vn.hung.xxxpre.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vn.hung.xxxpre.entity.key.MovieCategoryId;
import jakarta.persistence.*;

@Entity
@Table(name = "movie_categories_join")
public class MovieCategory {

    @EmbeddedId
    private MovieCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    @JsonBackReference // Child side
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    @JsonBackReference // Child side
    private Category category;

    public MovieCategory() {}

    public MovieCategory(Movie movie, Category category) {
        this.movie = movie;
        this.category = category;
        this.id = new MovieCategoryId(movie.getId(), category.getId());
    }

    public MovieCategoryId getId() { return id; }
    public void setId(MovieCategoryId id) { this.id = id; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}