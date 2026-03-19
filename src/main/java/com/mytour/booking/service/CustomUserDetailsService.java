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
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 1. Tìm kiếm User trong Database bằng userId
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với mã: " + userId));

        // 2. Chuyển đổi Role thành GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        // 3. Trả về đối tượng UserDetails (dùng userId làm username cho Spring Security)
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPasswordHash(),
                Collections.singletonList(authority)
        );
    }

    public Long getIdFromUserDetails(UserDetails userDetails) {
        User user = userRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found from security context"));
        return user.getId();
    }
}