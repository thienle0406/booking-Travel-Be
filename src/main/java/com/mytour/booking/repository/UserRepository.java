package com.mytour.booking.repository;

import com.mytour.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByCompanyId(String companyId);
    Optional<User> findByEmail(String email);
}