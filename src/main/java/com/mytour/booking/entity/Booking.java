package com.mytour.booking.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
@EqualsAndHashCode(callSuper = true)
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "departure_id", nullable = false)
    private String tourDepartureId;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Liên kết với User

    @Column(name = "driver_id")
    private String driverId; // Có thể null

    @Column(name = "company_id", nullable = false)
    private String companyId;

    // Thông tin khách hàng
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private int numberOfGuests;
    private double totalPrice;

    private LocalDate bookingDate;
    private String status; // Pending, Confirmed, Cancelled
}