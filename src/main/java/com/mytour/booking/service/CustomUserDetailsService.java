package com.mytour.booking.service;

import com.mytour.booking.entity.User;
import com.mytour.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // <-- Import quan trọng
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Phương thức này được Spring Security gọi khi cần xác thực người dùng.
     * Nó tìm kiếm User trong DB và trả về đối tượng UserDetails.
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // 1. Tìm kiếm User trong Database bằng username
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username hoặc email: " + usernameOrEmail));

        // 2. Chuyển đổi Role (trong Entity) thành GrantedAuthority (của Spring Security)
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        // 3. Trả về đối tượng UserDetails (đối tượng Security nội bộ)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(), // PHẢI LÀ PASSWORD ĐÃ MÃ HÓA
                Collections.singletonList(authority)
        );
    }
    public Long getUserIdFromUserDetails(UserDetails userDetails) {
        // Lấy User Entity dựa trên username (thông tin này đến từ JWT)
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found from security context"));
        return user.getId();
    }
}