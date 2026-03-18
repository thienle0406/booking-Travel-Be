package com.mytour.booking.service;

import com.mytour.booking.entity.Booking;
import com.mytour.booking.entity.TourDeparture;
import com.mytour.booking.model.request.BookingCreateRequest;
import com.mytour.booking.repository.BookingRepository;
import com.mytour.booking.repository.TourDepartureRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private TourDepartureRepository departureRepository;

    @Transactional
    public Booking updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String oldStatus = booking.getStatus();
        booking.setStatus(status);

        Optional<TourDeparture> departureOpt = departureRepository.findById(booking.getTourDepartureId());

        departureOpt.ifPresent(departure -> {
            if ("Confirmed".equals(status) && !"Confirmed".equals(oldStatus)) {
                departure.setBookedSlots(departure.getBookedSlots() + booking.getNumberOfGuests());
            }
            else if ("Cancelled".equals(status) && "Confirmed".equals(oldStatus)) {
                departure.setBookedSlots(departure.getBookedSlots() - booking.getNumberOfGuests());
            }
            departureRepository.save(departure);
        });

        return bookingRepository.save(booking);
    }

    public List<Booking> getConfirmedBookingsByMonth(String companyId, int year, int month) {
        // === FIX: CORRECT DATE CALCULATION ===
        List<Booking> allConfirmedBookings = bookingRepository.findByCompanyId(companyId).stream()
                .filter(b -> "Confirmed".equals(b.getStatus()))
                .collect(Collectors.toList());

        // FE sends month 0-11, but Java LocalDate uses 1-12
        return allConfirmedBookings.stream()
                .filter(b -> {
                    LocalDate date = b.getBookingDate();
                    // Fix: getYear() returns actual year (e.g. 2025), no need +1900
                    // Fix: getMonthValue() returns 1-12, FE sends 0-11, so add 1
                    return date.getYear() == year && (date.getMonthValue() - 1) == month;
                })
                .collect(Collectors.toList());
        // =====================================
    }

    @Transactional
    public Booking createBooking(Long userId, BookingCreateRequest request) {
        Optional<TourDeparture> departureOpt = departureRepository.findById(request.getTourDepartureId());

        if (!departureOpt.isPresent()) {
            throw new RuntimeException("Tour Departure không tồn tại.");
        }

        TourDeparture departure = departureOpt.get();
        if (departure.getTotalSlots() - departure.getBookedSlots() < request.getNumberOfGuests()) {
            throw new RuntimeException("Số chỗ còn lại không đủ.");
        }

        Booking booking = new Booking();
        BeanUtils.copyProperties(request, booking);

        booking.setUserId(userId);
        booking.setBookingDate(LocalDate.now());
        booking.setStatus("Pending");

        return bookingRepository.save(booking);
    }

    public List<Booking> findAllBookings(String companyId) {
        return bookingRepository.findByCompanyId(companyId);
    }

    public List<Booking> findBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> findBookingsByDriverId(String driverId) {
        return bookingRepository.findByDriverId(driverId);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        String oldStatus = booking.getStatus();

        Optional<TourDeparture> departureOpt = departureRepository.findById(booking.getTourDepartureId());

        departureOpt.ifPresent(departure -> {
            if ("Confirmed".equals(oldStatus)) {
                int newSlots = departure.getBookedSlots() - booking.getNumberOfGuests();
                departure.setBookedSlots(Math.max(0, newSlots)); // Prevent negative
                departureRepository.save(departure);
            }
        });

        bookingRepository.delete(booking);
    }

    @Transactional
    public Booking assignDriver(Long bookingId, String driverId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setDriverId(driverId);
        return bookingRepository.save(booking);
    }
}