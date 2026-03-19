package com.mytour.booking.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId; // Tên đăng nhập

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "full_name", nullable = false)
    private String fullName; // Họ và tên

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    // === Driver-specific fields (only used when role = DRIVER) ===
    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "vehicle_info")
    private String vehicleInfo;

    @Column(name = "driver_status")
    private String driverStatus; // available, busy (null if not a driver)
}