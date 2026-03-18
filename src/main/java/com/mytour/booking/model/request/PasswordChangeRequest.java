package com.mytour.booking.model.request;

import lombok.Data;

// Khớp với data gửi từ ProfilePage.tsx
@Data
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}