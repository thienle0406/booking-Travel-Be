package com.mytour.booking.service;

import com.mytour.booking.entity.Category;
import com.mytour.booking.exception.ResourceNotFoundException; // Giả định
import com.mytour.booking.exception.ValidationException; // Giả định
import com.mytour.booking.repository.CategoryRepository;
import com.mytour.booking.repository.TourTemplateRepository; // Cần thiết
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// Class này là Bean Service duy nhất, thay thế cho Interface và Impl
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TourTemplateRepository tourTemplateRepository;

    public List<Category> findAllCategories(String companyId) {
        return categoryRepository.findByCompanyId(companyId);
    }

    public Category findCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public Category saveCategory(Category category) {

        // LOGIC NGHIỆP VỤ: Kiểm tra trùng tên
        categoryRepository.findByNameAndCompanyId(category.getName(), category.getCompanyId())
                .ifPresent(existingCategory -> {
                    if (category.getId() == null || !existingCategory.getId().equals(category.getId())) {
                        throw new ValidationException("Tên Danh mục '" + category.getName() + "' đã tồn tại trong công ty.");
                    }
                });

        // Xử lý ID mới
        if (category.getId() == null || category.getId().isEmpty()) {
            category.setId(UUID.randomUUID().toString());
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(String id, Category categoryDetails) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Cập nhật và sử dụng lại logic saveCategory để áp dụng validation trùng tên
        existingCategory.setName(categoryDetails.getName());
        if (categoryDetails.getImageUrl() != null) {
            existingCategory.setImageUrl(categoryDetails.getImageUrl());
        }
        return saveCategory(existingCategory);
    }

    @Transactional
    public void deleteCategory(String id) {

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        // LOGIC NGHIỆP VỤ: Kiểm tra ràng buộc khóa ngoại
        long countTemplates = tourTemplateRepository.countByCategoryId(id);

        if (countTemplates > 0) {
            throw new ValidationException("Không thể xóa Danh mục vì có " + countTemplates + " Tour Templates đang sử dụng.");
        }

        categoryRepository.deleteById(id);
    }
}