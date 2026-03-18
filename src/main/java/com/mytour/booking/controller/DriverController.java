package com.mytour.booking.controller;

import com.mytour.booking.entity.Driver;
import com.mytour.booking.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    // === POST /api/v1/drivers/list ===
    // Khớp FE: apiService.drivers.getAll
    @PostMapping("/list")
    public ResponseEntity<List<Driver>> getAllDrivers(@RequestBody Map<String, String> request) {
        String companyId = request.get("companyId");

        if (companyId == null || companyId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Driver> drivers = driverService.findAllDrivers(companyId);
        return ResponseEntity.ok(drivers);
    }

    // === POST /api/v1/drivers (CREATE) ===
    @PostMapping
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        Driver created = driverService.createDriver(driver);
        return ResponseEntity.ok(created);
    }

    // === PUT /api/v1/drivers/{id} (UPDATE) ===
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable String id, @RequestBody Driver driverDetails) {
        Driver updated = driverService.updateDriver(id, driverDetails);
        return ResponseEntity.ok(updated);
    }

    // === DELETE /api/v1/drivers/{id} (DELETE) ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}