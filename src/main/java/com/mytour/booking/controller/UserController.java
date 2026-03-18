package com.mytour.booking.controller;

import com.mytour.booking.entity.User;
import com.mytour.booking.model.request.PasswordChangeRequest; // Cần tạo DTO này
import com.mytour.booking.service.CustomUserDetailsService;
import com.mytour.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private CustomUserDetailsService userDetailsService;

    // === 1. POST /api/v1/users/list (Admin: Lấy danh sách users) ===
    // Khớp FE: apiService.users.getAll
    @PostMapping("/list")
    public ResponseEntity<List<User>> getAllUsers(@RequestBody Map<String, String> request) {
        List<User> users = userService.findAllUsers(request.get("companyId"));
        return ResponseEntity.ok(users);
    }

    // === 2. PUT /api/v1/users/{id}/profile (User/Admin: Sửa tên/email) ===
    // Khớp FE: apiService.updateProfile
    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User userDetails) {
        // [GHI CHÚ] Java BE cần logic để đảm bảo ID userDetails khớp với ID trong JWT
        User updatedUser = userService.updateUserProfile(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // === 3. PUT /api/v1/users/{id}/change-password (User/Admin: Đổi mật khẩu) ===
    // Khớp FE: apiService.changePassword
    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody PasswordChangeRequest request) {
        // [GHI CHÚ] Java BE cần logic để kiểm tra mật khẩu cũ
        userService.changeUserPassword(id, request.getOldPassword(), request.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đổi mật khẩu thành công!");
        return ResponseEntity.ok(response);
    }

    // === 4. PUT /api/v1/users/{id}/admin (Admin: cập nhật user đầy đủ, bao gồm role) ===
    @PutMapping("/{id}/admin")
    public ResponseEntity<User> updateUserAdmin(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUserAdmin(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // === 5. DELETE /api/v1/users/{id} (Admin: xóa user) ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAdmin(@PathVariable Long id) {
        userService.deleteUserAdmin(id);
        return ResponseEntity.noContent().build();
    }
}