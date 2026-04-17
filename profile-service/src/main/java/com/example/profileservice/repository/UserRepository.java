package com.example.profileservice.repository;

import com.example.profileservice.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
    List<User> findByRoleType(String roleType);
    Optional<User> findByEmail(String email);
}
