package com.mytour.booking.service;

import com.mytour.booking.config.JwtTokenProvider;
import com.mytour.booking.entity.User;
import com.mytour.booking.entity.Role;
import com.mytour.booking.model.reponse.LoginResponse;
import com.mytour.booking.repository.UserRepository;
import com.mytour.booking.model.request.LoginRequest;
import com.mytour.booking.model.request.RegisterRequest;
// GIẢ ĐỊNH: Import lớp JwtTokenProvider đã được tạo

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; // Để xử lý lỗi
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Để xử lý lỗi
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Tốt cho các thao tác DB

@Service
public class AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // ĐÃ THÊM: Sử dụng JwtTokenProvider thật
    @Autowired private JwtTokenProvider tokenProvider;

    // === 1. ĐĂNG NHẬP (FE: POST /auth/login) ===
    public LoginResponse loginUser(LoginRequest loginRequest) {

        Authentication authentication;

        // 1. Xác thực người dùng bằng userId
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Lấy thông tin user đầy đủ
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User không được tìm thấy sau khi xác thực thành công."));

        // 3. Tạo JWT Token
        String token = tokenProvider.generateToken(authentication);

        // 4. Trả về response
        return new LoginResponse(token, user);
    }

    // === 2. ĐĂNG KÝ (FE: POST /auth/register) ===
    @Transactional // Đảm bảo mọi thay đổi DB được thực hiện nguyên vẹn
    public User registerUser(RegisterRequest registerRequest) {

        // 1. Kiểm tra userId đã tồn tại chưa
        if (userRepository.findByUserId(registerRequest.getUserId()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại.");
        }

        // 2. Kiểm tra Email đã tồn tại chưa
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng.");
        }

        // 3. Tạo Entity User mới
        User user = new User();
        user.setUserId(registerRequest.getUserId());
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCompanyId(registerRequest.getCompanyId());
        user.setRole(Role.USER);

        return userRepository.save(user);
    }
}