package com.vn.hung.xxxpre.repository;

import com.vn.hung.xxxpre.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {

    @Query(value = "SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN m.movieCategories mc " +
            "LEFT JOIN mc.category c " +
            "LEFT JOIN m.movieActors ma " +
            "LEFT JOIN ma.actor a " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))",

            // FIX: Explicitly define how to count the total records for pagination
            countQuery = "SELECT COUNT(DISTINCT m) FROM Movie m " +
                    "LEFT JOIN m.movieCategories mc " +
                    "LEFT JOIN mc.category c " +
                    "LEFT JOIN m.movieActors ma " +
                    "LEFT JOIN ma.actor a " +
                    "WHERE (:keyword IS NULL OR :keyword = '' OR " +
                    "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Movie> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}