package com.mytour.booking.controller;

import com.mytour.booking.entity.TourDeparture;
import com.mytour.booking.model.request.TourSearchRequest;
import com.mytour.booking.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tour-departures")
public class TourDepartureController {

    @Autowired
    private TourService tourService;

    // === 1. POST /api/v1/tour-departures/list (Admin) ===
    // Khớp FE: apiService.tourDepartures.getAll
    @PostMapping("/list")
    public ResponseEntity<List<TourDeparture>> getAllDepartures(@RequestBody TourSearchRequest request) {
        List<TourDeparture> departures = tourService.findAllDepartures(request.getCompanyId());
        return ResponseEntity.ok(departures);
    }

    // === 2. POST /api/v1/tour-departures/detail (FE: Trang chi tiết) ===
    // Khớp FE: apiService.tourDepartures.getOne
    @PostMapping("/detail")
    public ResponseEntity<TourDeparture> getDepartureDetail(@RequestBody TourSearchRequest request) {
        TourDeparture departure = tourService.findDepartureById(request.getId());
        return ResponseEntity.ok(departure);
    }

    // === XÓA MỤC 3 Ở ĐÂY: POST /search ĐÃ ĐƯỢC CHUYỂN SANG TourController ===

    // === 4. POST /api/v1/tour-departures (CREATE) ===
    @PostMapping
    public ResponseEntity<TourDeparture> createDeparture(@RequestBody TourDeparture departure) {
        TourDeparture savedDeparture = tourService.saveDeparture(departure);
        return ResponseEntity.ok(savedDeparture);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourDeparture> updateDeparture(@PathVariable String id, @RequestBody TourDeparture departureDetails) {
        TourDeparture updatedDeparture = tourService.updateDeparture(id, departureDetails);
        return ResponseEntity.ok(updatedDeparture);
    }

    // === 2. DELETE /api/v1/tour-departures/{id} (DELETE) ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeparture(@PathVariable String id) {
        tourService.deleteDeparture(id);
        return ResponseEntity.noContent().build();
    }

    // === BỔ SUNG: POST /api/v1/tour-departures/manifest (Khớp FE: getDriverManifest) ===
    @PostMapping("/manifest")
    public ResponseEntity<?> getDriverManifest(@RequestBody TourSearchRequest request) {
        // [FE gửi]: { departureId }
        Object manifest = tourService.getDriverManifest(request.getId()); // Giả định dùng getId cho departureId
        return ResponseEntity.ok(manifest);
    }
}