package com.mcqportal.repository;

import com.mcqportal.entity.Role;
import com.mcqportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(Role role);
    Page<User> findByFullnameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullname, String email, Pageable pageable);
}
