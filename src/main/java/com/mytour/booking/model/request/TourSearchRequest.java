package com.mytour.booking.model.request;

import lombok.Data;

@Data
public class TourSearchRequest {
    private String companyId;
    // Lọc theo categoryId cho chuẩn (khớp với Category.id / TourTemplate.categoryId)
    private String categoryId;
    // (Tùy chọn) Vẫn giữ category name nếu sau này cần tìm theo text
    private String category;
    private String destination;
    private String id; // <-- THÊM ID (cho detail API)
    private Long userId;
}