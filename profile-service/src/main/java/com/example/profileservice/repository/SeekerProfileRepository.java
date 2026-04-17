package com.example.profileservice.repository;


import com.example.profileservice.model.SeekerProfile;
import com.example.profileservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeekerProfileRepository extends JpaRepository<SeekerProfile, Long> {
    Optional<SeekerProfile> findByUser(User user);
}