package com.vn.hung.xxxpre.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MovieActorId implements Serializable {

    @Column(name = "movie_id")
    private String movieId;

    @Column(name = "actor_id")
    private String actorId;

    public MovieActorId() {}

    public MovieActorId(String movieId, String actorId) {
        this.movieId = movieId;
        this.actorId = actorId;
    }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieActorId that = (MovieActorId) o;
        return Objects.equals(movieId, that.movieId) &&
                Objects.equals(actorId, that.actorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, actorId);
    }
}