package com.mytour.booking.model.request;

import lombok.Data;

// Khớp với data gửi từ AccountingPage.tsx
@Data
public class RevenueReportRequest {
    private String companyId;
    private Integer year;
    private Integer month; // 0-11 (Khớp với FE)
}