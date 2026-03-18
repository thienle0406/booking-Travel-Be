package com.mytour.booking.repository;

import com.mytour.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Lấy danh sách booking của một người dùng (My Bookings trên FE)
    List<Booking> findByUserId(Long userId);

    // Lấy danh sách booking của một công ty
    List<Booking> findByCompanyId(String companyId);

    // Lấy danh sách booking theo tài xế
    List<Booking> findByDriverId(String driverId);
}