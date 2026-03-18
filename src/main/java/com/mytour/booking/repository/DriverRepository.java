package com.mytour.booking.repository;

import com.mytour.booking.entity.Driver; // Giả định Driver Entity tồn tại
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

    /**
     * Khớp với yêu cầu Service: Tìm tất cả Driver thuộc về một công ty.
     */
    List<Driver> findByCompanyId(String companyId);
}