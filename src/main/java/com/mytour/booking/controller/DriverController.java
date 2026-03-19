package com.mytour.booking.controller;

import com.mytour.booking.entity.Booking;
import com.mytour.booking.entity.User;
import com.mytour.booking.service.BookingService;
import com.mytour.booking.service.CustomUserDetailsService;
import com.mytour.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    @Autowired private UserService userService;
    @Autowired private BookingService bookingService;
    @Autowired private CustomUserDetailsService userDetailsService;

    // === POST /api/v1/drivers/list ===
    // Trả về danh sách User có role = DRIVER
    @PostMapping("/list")
    public ResponseEntity<List<User>> getAllDrivers(@RequestBody Map<String, String> request) {
        String companyId = request.get("companyId");
        if (companyId == null || companyId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<User> drivers = userService.findAllDrivers(companyId);
        return ResponseEntity.ok(drivers);
    }

    // === GET /api/v1/drivers/my-bookings ===
    // Tài xế xem danh sách booking được gán cho mình
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userDetailsService.getIdFromUserDetails(userDetails);
        List<Booking> bookings = bookingService.findBookingsByDriverId(String.valueOf(userId));
        return ResponseEntity.ok(bookings);
    }
}
