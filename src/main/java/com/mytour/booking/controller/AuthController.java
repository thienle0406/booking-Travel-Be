package com.mytour.booking.controller;

import com.mytour.booking.model.reponse.LoginResponse;
import com.mytour.booking.model.request.LoginRequest;
import com.mytour.booking.model.request.RegisterRequest;
import com.mytour.booking.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth") // Khớp với cấu hình SecurityConfig.java
public class AuthController {

    @Autowired
    private AuthService authService;

    // === API: POST /api/v1/auth/login (Khớp FE) ===
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // [GHI CHÚ] FE gửi username và password. BE trả về JWT Token và User object.
        LoginResponse response = authService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    // === API: POST /api/v1/auth/register (Khớp FE) ===
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok("Đăng ký thành công!");
    }
}