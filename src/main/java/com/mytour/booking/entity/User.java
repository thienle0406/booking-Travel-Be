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

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // === FIX: ADD MISSING FIELDS FOR FRONTEND ===
    @Column(name = "avatar")
    private String avatar; // Profile picture URL

    @Column(name = "phone")
    private String phone; // Phone number

    @Column(name = "address")
    private String address; // User address
    // ============================================
}