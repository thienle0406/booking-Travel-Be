package com.mytour.booking.model.reponse;

import com.mytour.booking.entity.User; // Cần import User Entity
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User user; // Gửi toàn bộ đối tượng User về FE
}