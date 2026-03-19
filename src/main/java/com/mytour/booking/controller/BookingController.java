package com.mytour.booking.controller;

import com.mytour.booking.entity.Booking;
import com.mytour.booking.model.request.BookingCreateRequest;
import com.mytour.booking.model.request.RevenueReportRequest;
import com.mytour.booking.service.BookingService;
import com.mytour.booking.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired private BookingService bookingService;
    @Autowired private CustomUserDetailsService userDetailsService; // Để lấy User ID

    // === 1. POST /api/v1/bookings (User tạo Booking) ===
    // Khớp FE: apiService.bookings.createForUser
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingCreateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        // Lấy userId từ JWT (rất quan trọng)
        Long userId = userDetailsService.getIdFromUserDetails(userDetails);

        // GỌI HÀM ĐÃ ĐƯỢC CODE
        Booking newBooking = bookingService.createBooking(userId, request);
        return ResponseEntity.ok(newBooking);
    }

    // === 2. POST /api/v1/bookings/list (Admin: Lấy tất cả) ===
    // Khớp FE: apiService.bookings.getAll
    @PostMapping("/list")
    public ResponseEntity<List<Booking>> getAllBookings(@RequestBody Map<String, String> request) {
        List<Booking> bookings = bookingService.findAllBookings(request.get("companyId"));
        return ResponseEntity.ok(bookings);
    }

    // === 3. POST /api/v1/bookings/my-bookings (User: Lấy booking của tôi) ===
    // Khớp FE: apiService.bookings.getByUserId
    @PostMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userDetailsService.getIdFromUserDetails(userDetails);
        List<Booking> bookings = bookingService.findBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    // === 3b. GET /api/v1/bookings/by-driver/{driverId} (Doanh thu theo tài xế) ===
    @GetMapping("/by-driver/{driverId}")
    public ResponseEntity<List<Booking>> getBookingsByDriver(@PathVariable String driverId) {
        List<Booking> bookings = bookingService.findBookingsByDriverId(driverId);
        return ResponseEntity.ok(bookings);
    }

    // === 4. PUT /api/v1/bookings/{id}/status (Admin: Duyệt/Hủy) ===
    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        Booking updatedBooking = bookingService.updateBookingStatus(id, statusMap.get("status"));
        return ResponseEntity.ok(updatedBooking);
    }

    // === 5. POST /api/v1/bookings/revenue-report (Admin: Báo cáo Kế toán) ===
    @PostMapping("/revenue-report")
    public ResponseEntity<List<Booking>> getRevenueReport(@RequestBody RevenueReportRequest request) {
        // Khớp FE: { companyId, year, month }
        List<Booking> bookings = bookingService.getConfirmedBookingsByMonth(
                request.getCompanyId(),
                request.getYear(),
                request.getMonth()
        );
        return ResponseEntity.ok(bookings);
    }

    // === 6. DELETE /api/v1/bookings/{id} ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/assign-driver")
    public ResponseEntity<Booking> assignDriver(@PathVariable Long id, @RequestBody Map<String, String> driverMap) {
        // ID booking dùng Long, driverId dùng String (vì ID tài xế thường là UUID/String)
        Booking updatedBooking = bookingService.assignDriver(id, driverMap.get("driverId"));
        return ResponseEntity.ok(updatedBooking);
    }
}