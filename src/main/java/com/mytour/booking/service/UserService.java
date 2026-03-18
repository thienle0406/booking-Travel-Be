package com.mytour.booking.service;

import com.mytour.booking.entity.User;
import com.mytour.booking.model.request.PasswordChangeRequest;
import com.mytour.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<User> findAllUsers(String companyId) {
        // [FE: POST /api/v1/users/list]
        return userRepository.findByCompanyId(companyId);
    }

    public User updateUserProfile(Long userId, User userDetails) {
        // [FE: PUT /api/v1/users/{id}/profile]
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());

        return userRepository.save(user);
    }

    public void changeUserPassword(Long userId, String oldPassword, String newPassword) {
        // [FE: PUT /api/v1/users/{id}/change-password]
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Kiểm tra mật khẩu cũ (Quan trọng)
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác.");
        }

        // 2. Mã hóa và lưu mật khẩu mới
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // === Admin: Cập nhật user (bao gồm role, phone, address, avatar) ===
    public User updateUserAdmin(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setAvatar(userDetails.getAvatar());

        return userRepository.save(user);
    }

    // === Admin: Xóa user ===
    public void deleteUserAdmin(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }
}