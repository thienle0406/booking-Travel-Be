package com.mytour.booking.controller;

import com.mytour.booking.entity.Category;
import com.mytour.booking.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // === 1. POST /api/v1/categories/list (Admin & FE) ===
    // Khớp FE: apiService.categories.getAll
    @PostMapping("/list")
    public ResponseEntity<List<Category>> getAllCategories(@RequestBody Map<String, String> request) {
        String companyId = request.get("companyId");
        if (companyId == null || companyId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Category> categories = categoryService.findAllCategories(companyId);
        return ResponseEntity.ok(categories);
    }

    // === 2. POST /api/v1/categories (CREATE) ===
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    // === 3. PUT /api/v1/categories/{id} (UPDATE) ===
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable String id,
            @RequestBody Category categoryDetails) {

        Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
        return ResponseEntity.ok(updatedCategory);
    }

    // === 4. DELETE /api/v1/categories/{id} (DELETE) ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // === 5. GET /api/v1/categories/{id} (DETAIL) ===
    // Thêm endpoint chi tiết nếu cần
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        Category category = categoryService.findCategoryById(id);
        return ResponseEntity.ok(category);
    }
}