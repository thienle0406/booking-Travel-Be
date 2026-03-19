package com.mytour.booking.model.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userId;    // Tên đăng nhập
    private String fullName;  // Họ và tên
    private String email;
    private String password;
    private String companyId;
}