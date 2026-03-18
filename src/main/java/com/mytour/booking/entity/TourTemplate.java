package com.mytour.booking.entity;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tour_templates")
@Data
@EqualsAndHashCode(callSuper = true)
public class TourTemplate extends BaseEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore // Tránh vòng lặp vô hạn khi serialize JSON (Category -> TourTemplates -> Category -> ...)
    private Category category;

    private String name;

    private String destination;

    @Column(name = "description_html", columnDefinition = "TEXT")
    private String descriptionHtml; 

    private String imageUrl;

    private double defaultPrice;

    private int discountPercent;
}