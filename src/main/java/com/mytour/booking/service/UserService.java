package com.mytour.booking.service;

import com.mytour.booking.entity.Role;
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
        return userRepository.findByCompanyId(companyId);
    }

    // === Lấy danh sách tài xế (User có role = DRIVER) ===
    public List<User> findAllDrivers(String companyId) {
        return userRepository.findByCompanyIdAndRole(companyId, Role.DRIVER);
    }

    public User updateUserProfile(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setGender(userDetails.getGender());

        return userRepository.save(user);
    }

    public void changeUserPassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // === Admin: Cập nhật user (bao gồm role, driver fields) ===
    public User updateUserAdmin(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setAvatar(userDetails.getAvatar());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setGender(userDetails.getGender());

        // Driver-specific fields
        user.setLicensePlate(userDetails.getLicensePlate());
        user.setVehicleInfo(userDetails.getVehicleInfo());
        user.setDriverStatus(userDetails.getDriverStatus());

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
