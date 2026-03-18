package com.mytour.booking.controller;

import com.mytour.booking.model.request.TourSearchRequest;
import com.mytour.booking.model.response.TourSearchResponse;
import com.mytour.booking.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tours") // Base path MỚI
public class TourController {

    @Autowired
    private TourService tourService;

    // === POST /api/v1/tours/search (API JOIN cho Trang chủ/Trang List) ===
    // Khớp FE: apiService.tourDepartures.getJoinedDepartures
    @PostMapping("/search")
    public ResponseEntity<List<TourSearchResponse>> searchTours(@RequestBody TourSearchRequest request) {
        // [FE gửi]: { companyId, category, destination }
        List<TourSearchResponse> tours = tourService.searchAndJoinTours(request);
        return ResponseEntity.ok(tours);
    }
}