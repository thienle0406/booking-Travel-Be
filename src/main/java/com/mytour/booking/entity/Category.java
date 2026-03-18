package com.mytour.booking.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;


@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_id", "name"})
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Không cần trả danh sách TourTemplate khi load Category -> tránh LazyInitialization/vòng lặp
    private java.util.Set<TourTemplate> tourTemplates;
}