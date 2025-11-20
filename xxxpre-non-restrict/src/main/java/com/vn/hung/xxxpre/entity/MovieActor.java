package com.vn.hung.xxxpre.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vn.hung.xxxpre.entity.key.MovieActorId;
import jakarta.persistence.*;

@Entity
@Table(name = "movie_actors_join")
public class MovieActor {

    @EmbeddedId
    private MovieActorId id;

    // --- FIX START ---
    // REMOVED: @Column(name = "movie_id") -> This caused the error

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId") // Matches the field name in MovieActorId
    @JoinColumn(name = "movie_id")
    private Movie movie;
    // --- FIX END ---

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actorId")
    @JoinColumn(name = "actor_id")
    @JsonBackReference // Child side
    private Actor actor;

    public MovieActor() {}

    public MovieActor(Movie movie, Actor actor) {
        this.movie = movie;
        this.actor = actor;
        this.id = new MovieActorId(movie.getId(), actor.getId());
    }

    public MovieActorId getId() { return id; }
    public void setId(MovieActorId id) { this.id = id; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public Actor getActor() { return actor; }
    public void setActor(Actor actor) { this.actor = actor; }
}