package com.mytour.booking.repository;

import com.mytour.booking.entity.TourDeparture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, String> {
    List<TourDeparture> findByCompanyId(String companyId);
}