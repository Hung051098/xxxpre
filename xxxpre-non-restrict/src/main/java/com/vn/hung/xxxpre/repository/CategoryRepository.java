package com.vn.hung.xxxpre.repository;

import com.vn.hung.xxxpre.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByName(String name);
}