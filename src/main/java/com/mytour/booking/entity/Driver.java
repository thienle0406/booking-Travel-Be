package com.mytour.booking.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;


@Entity
@Table(name = "drivers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Driver extends BaseEntity {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    private String name;
    private String phone;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "vehicle_info")
    private String vehicleInfo;

    private String status; // available, busy
}