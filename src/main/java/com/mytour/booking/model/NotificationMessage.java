package com.mytour.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
    private String type;       // NEW_BOOKING, BOOKING_CONFIRMED, DRIVER_ASSIGNED, BOOKING_CANCELLED
    private String title;
    private String message;
    private String bookingId;
    private String targetRole; // ADMIN, DRIVER, USER
    private String targetId;   // userId hoac driverId cu the (nullable)
    private long timestamp;
}