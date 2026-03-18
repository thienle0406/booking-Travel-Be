package com.mytour.booking.repository;

import com.mytour.booking.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    /** Khớp FE: categories.getAll */
    List<Category> findByCompanyId(String companyId);

    /** Dùng để kiểm tra ràng buộc duy nhất (Business Logic) */
    Optional<Category> findByNameAndCompanyId(String name, String companyId);
}