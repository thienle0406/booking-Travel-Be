package com.mytour.booking.service;

import com.mytour.booking.entity.Booking;
import com.mytour.booking.entity.Driver;
import com.mytour.booking.entity.TourDeparture;
import com.mytour.booking.entity.TourTemplate;
import com.mytour.booking.model.request.BookingCreateRequest;
import com.mytour.booking.repository.BookingRepository;
import com.mytour.booking.repository.DriverRepository;
import com.mytour.booking.repository.TourDepartureRepository;
import com.mytour.booking.repository.TourTemplateRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private TourDepartureRepository departureRepository;
    @Autowired private TourTemplateRepository templateRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private EmailService emailService;

    private static final DateTimeFormatter VN_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Helper: lay ten tour tu departure
    private String getTourName(Booking booking) {
        try {
            TourDeparture dep = departureRepository.findById(booking.getTourDepartureId()).orElse(null);
            if (dep != null) {
                TourTemplate tmpl = templateRepository.findById(dep.getTourTemplateId()).orElse(null);
                if (tmpl != null) return tmpl.getName();
            }
        } catch (Exception ignored) {}
        return "Tour #" + booking.getTourDepartureId();
    }

    // Helper: lay ngay khoi hanh
    private String getStartDate(Booking booking) {
        try {
            TourDeparture dep = departureRepository.findById(booking.getTourDepartureId()).orElse(null);
            if (dep != null && dep.getStartDate() != null) return dep.getStartDate().format(VN_DATE);
        } catch (Exception ignored) {}
        return "";
    }

    // =========================================================
    // TAO BOOKING MOI (User dat tour)
    // =========================================================
    @Transactional
    public Booking createBooking(Long userId, BookingCreateRequest request) {
        Optional<TourDeparture> departureOpt = departureRepository.findById(request.getTourDepartureId());

        if (!departureOpt.isPresent()) {
            throw new RuntimeException("Tour Departure khong ton tai.");
        }

        TourDeparture departure = departureOpt.get();
        if (departure.getTotalSlots() - departure.getBookedSlots() < request.getNumberOfGuests()) {
            throw new RuntimeException("So cho con lai khong du.");
        }

        Booking booking = new Booking();
        BeanUtils.copyProperties(request, booking);
        booking.setUserId(userId);
        booking.setBookingDate(LocalDate.now());
        booking.setStatus("Pending");

        Booking saved = bookingRepository.save(booking);

        // --- THONG BAO + EMAIL ---
        String tourName = getTourName(saved);
        String bookingId = String.valueOf(saved.getId());

        // WebSocket -> Admin
        notificationService.notifyAdmins(
                "NEW_BOOKING",
                "Booking moi #" + bookingId,
                "Khach " + saved.getCustomerName() + " dat " + tourName,
                bookingId
        );

        // Email -> Admin (gui ngam, khong block)
        emailService.sendNewBookingToAdmin(
                "admin@mytour.vn",
                saved.getCustomerName(),
                saved.getCustomerPhone(),
                tourName,
                saved.getTotalPrice(),
                bookingId
        );

        return saved;
    }

    // =========================================================
    // CAP NHAT TRANG THAI BOOKING
    // =========================================================
    @Transactional
    public Booking updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String oldStatus = booking.getStatus();
        booking.setStatus(status);

        Optional<TourDeparture> departureOpt = departureRepository.findById(booking.getTourDepartureId());
        String tourName = getTourName(booking);
        String startDate = getStartDate(booking);
        String bkId = String.valueOf(booking.getId());

        // --- Logic cap nhat so cho (bookedSlots) ---
        departureOpt.ifPresent(departure -> {
            if ("Confirmed".equals(status) && !"Confirmed".equals(oldStatus)
                    && !"Assigned".equals(oldStatus) && !"InProgress".equals(oldStatus)) {
                departure.setBookedSlots(departure.getBookedSlots() + booking.getNumberOfGuests());
                departureRepository.save(departure);
            }
            if ("Cancelled".equals(status) &&
                    ("Confirmed".equals(oldStatus) || "Assigned".equals(oldStatus) || "InProgress".equals(oldStatus))) {
                int newSlots = departure.getBookedSlots() - booking.getNumberOfGuests();
                departure.setBookedSlots(Math.max(0, newSlots));
                departureRepository.save(departure);
            }
        });

        // --- Logic tu dong cap nhat trang thai tai xe ---
        String driverId = booking.getDriverId();
        if (driverId != null && !driverId.isEmpty()) {
            driverRepository.findById(driverId).ifPresent(driver -> {
                if ("Assigned".equals(status) || "InProgress".equals(status)) {
                    driver.setStatus("busy");
                    driverRepository.save(driver);
                }
                if ("Completed".equals(status) || "Cancelled".equals(status)) {
                    driver.setStatus("available");
                    driverRepository.save(driver);
                }
            });
        }

        Booking saved = bookingRepository.save(booking);

        // --- THONG BAO + EMAIL theo trang thai ---
        if ("Confirmed".equals(status)) {
            // WebSocket + Email -> User
            notificationService.notifyUser(
                    booking.getUserId(), "BOOKING_CONFIRMED",
                    "Booking da xac nhan!",
                    "Booking #" + bkId + " - " + tourName + " da duoc duyet",
                    bkId
            );
            emailService.sendBookingConfirmed(
                    booking.getCustomerEmail(), booking.getCustomerName(),
                    tourName, startDate, booking.getTotalPrice(), bkId
            );
        }

        if ("Cancelled".equals(status)) {
            // WebSocket + Email -> User
            String refund = "Lien he hotline 0338 739 493 de duoc ho tro hoan tien";
            notificationService.notifyUser(
                    booking.getUserId(), "BOOKING_CANCELLED",
                    "Booking da bi huy",
                    "Booking #" + bkId + " - " + tourName + " da bi huy",
                    bkId
            );
            emailService.sendBookingCancelled(
                    booking.getCustomerEmail(), booking.getCustomerName(),
                    tourName, bkId, refund
            );
        }

        if ("Completed".equals(status)) {
            notificationService.notifyUser(
                    booking.getUserId(), "BOOKING_COMPLETED",
                    "Tour da hoan thanh!",
                    tourName + " da ket thuc. Cam on ban!",
                    bkId
            );
        }

        return saved;
    }

    // =========================================================
    // GAN TAI XE
    // =========================================================
    @Transactional
    public Booking assignDriver(Long bookingId, String driverId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Neu co tai xe cu, chuyen ve available
        String oldDriverId = booking.getDriverId();
        if (oldDriverId != null && !oldDriverId.isEmpty() && !oldDriverId.equals(driverId)) {
            driverRepository.findById(oldDriverId).ifPresent(oldDriver -> {
                oldDriver.setStatus("available");
                driverRepository.save(oldDriver);
            });
        }

        booking.setDriverId(driverId);

        // Chuyen tai xe moi -> busy + thong bao
        if (driverId != null && !driverId.isEmpty()) {
            driverRepository.findById(driverId).ifPresent(newDriver -> {
                newDriver.setStatus("busy");
                driverRepository.save(newDriver);

                String tourName = getTourName(booking);
                String startDate = getStartDate(booking);
                String bkId = String.valueOf(booking.getId());

                // WebSocket -> Tai xe
                notificationService.notifyDriver(
                        driverId,
                        "Ban duoc gan tour moi!",
                        tourName + " - Khoi hanh " + startDate,
                        bkId
                );

                // Email -> Tai xe (neu co email, hien tai Driver chua co field email)
                // emailService.sendDriverAssigned(...)
            });
        }

        return bookingRepository.save(booking);
    }

    // =========================================================
    // CAC HAM KHAC (giu nguyen)
    // =========================================================
    public List<Booking> getConfirmedBookingsByMonth(String companyId, int year, int month) {
        List<Booking> allConfirmedBookings = bookingRepository.findByCompanyId(companyId).stream()
                .filter(b -> "Confirmed".equals(b.getStatus()))
                .collect(Collectors.toList());
        return allConfirmedBookings.stream()
                .filter(b -> {
                    LocalDate date = b.getBookingDate();
                    return date.getYear() == year && (date.getMonthValue() - 1) == month;
                })
                .collect(Collectors.toList());
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
        departureRepository.findById(booking.getTourDepartureId()).ifPresent(departure -> {
            if ("Confirmed".equals(oldStatus) || "Assigned".equals(oldStatus)) {
                int newSlots = departure.getBookedSlots() - booking.getNumberOfGuests();
                departure.setBookedSlots(Math.max(0, newSlots));
                departureRepository.save(departure);
            }
        });
        bookingRepository.delete(booking);
    }
}
