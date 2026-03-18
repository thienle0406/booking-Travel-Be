package com.mytour.booking.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TourSearchResponse {
    private String id;
    private String name;
    private String destination;
    private String duration;
    private double price;

    // === BỔ SUNG 2 TRƯỜNG NÀY ===
    private double originalPrice;
    private int discountPercent;
    // ============================

    private double rating;
    private String description;
    private String imageUrl;
    private String category;
}