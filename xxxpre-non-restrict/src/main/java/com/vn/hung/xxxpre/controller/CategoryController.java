package com.vn.hung.xxxpre.controller;

import com.vn.hung.xxxpre.entity.Category;
import com.vn.hung.xxxpre.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Lists all categories available in the database.
     * GET /api/v1/categories
     */
    @GetMapping
    public ResponseEntity<List<Category>> listAllCategories() {
        List<Category> categories = categoryService.listAllCategories();
        return ResponseEntity.ok(categories);
    }
}