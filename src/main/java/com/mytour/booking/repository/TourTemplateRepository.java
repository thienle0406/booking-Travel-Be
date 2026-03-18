package com.mytour.booking.repository;

import com.mytour.booking.entity.TourTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TourTemplateRepository extends JpaRepository<TourTemplate, String> {
    List<TourTemplate> findByCompanyId(String companyId);
    long countByCategoryId(String categoryId);
}