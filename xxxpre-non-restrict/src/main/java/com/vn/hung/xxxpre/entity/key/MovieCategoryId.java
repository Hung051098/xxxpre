package com.vn.hung.xxxpre.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MovieCategoryId implements Serializable {

    @Column(name = "movie_id")
    private String movieId;

    @Column(name = "category_id")
    private String categoryId;

    public MovieCategoryId() {}

    public MovieCategoryId(String movieId, String categoryId) {
        this.movieId = movieId;
        this.categoryId = categoryId;
    }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCategoryId that = (MovieCategoryId) o;
        return Objects.equals(movieId, that.movieId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, categoryId);
    }
}