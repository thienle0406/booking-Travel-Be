package com.mytour.booking.repository;

import com.mytour.booking.entity.Role;
import com.mytour.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    List<User> findByCompanyId(String companyId);
    List<User> findByCompanyIdAndRole(String companyId, Role role);
    Optional<User> findByEmail(String email);
    long countByCompanyId(String companyId);
}