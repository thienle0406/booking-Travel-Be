package com.mytour.booking.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "tour_departures")
@Data
@EqualsAndHashCode(callSuper = true)
public class TourDeparture extends BaseEntity {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "template_id", nullable = false)
    private String tourTemplateId;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    private LocalDate startDate;
    private LocalDate endDate;

    private double originalPrice;
    private int discountPercent;
    private double price; // Giá cuối cùng

    private int totalSlots;
    private int bookedSlots;

    @Column(nullable = false)
    private String status; // Pending, Confirmed, Completed, Cancelled
}