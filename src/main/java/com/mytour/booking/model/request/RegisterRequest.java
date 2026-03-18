package com.mytour.booking.model.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String companyId; // Khớp với FE
}