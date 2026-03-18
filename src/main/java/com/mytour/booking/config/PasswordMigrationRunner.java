package com.mytour.booking.config;

import com.mytour.booking.entity.User;
import com.mytour.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Chạy một lần để migrate toàn bộ password trong DB sang dạng BCrypt.
 * Logic:
 * - Nếu password_hash không bắt đầu bằng "$2a$", "$2b$", "$2y$" thì coi là plain-text
 * - Encode lại bằng PasswordEncoder (BCrypt) và lưu lại.
 */
@Configuration
public class PasswordMigrationRunner {

    @Bean
    public CommandLineRunner migratePasswords(UserRepository userRepository,
                                              PasswordEncoder passwordEncoder) {
        return args -> {
            List<User> users = userRepository.findAll();
            int updatedCount = 0;

            for (User user : users) {
                String hash = user.getPasswordHash();
                if (hash == null || hash.isEmpty()) {
                    continue;
                }

                // Nếu password chưa ở dạng BCrypt (thường BCrypt có prefix $2a$, $2b$, $2y$)
                if (!hash.startsWith("$2a$") && !hash.startsWith("$2b$") && !hash.startsWith("$2y$")) {
                    String encoded = passwordEncoder.encode(hash);
                    user.setPasswordHash(encoded);
                    updatedCount++;
                }
            }

            if (updatedCount > 0) {
                userRepository.saveAll(users);
                System.out.println("Password migration: updated " + updatedCount + " users to BCrypt.");
            } else {
                System.out.println("Password migration: no users needed update.");
            }
        };
    }
}

