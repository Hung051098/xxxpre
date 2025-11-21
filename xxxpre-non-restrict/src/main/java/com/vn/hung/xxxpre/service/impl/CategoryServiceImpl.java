package com.vn.hung.xxxpre.service.impl;

import com.vn.hung.xxxpre.entity.Category;
import com.vn.hung.xxxpre.repository.CategoryRepository;
import com.vn.hung.xxxpre.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> listAllCategories() {
        // JpaRepository.findAll() tự động trả về tất cả các bản ghi
        return categoryRepository.findAll();
    }
}