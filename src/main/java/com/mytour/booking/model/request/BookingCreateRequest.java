package com.mytour.booking.model.request;

import lombok.Data;

// Khớp với data gửi từ BookingPage.tsx
@Data
public class BookingCreateRequest {
    private String tourDepartureId;
    private String companyId; // FE gửi companyId

    // Thông tin khách hàng (FE lấy từ Profile hoặc form)
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private int numberOfGuests;
    private double totalPrice;
}