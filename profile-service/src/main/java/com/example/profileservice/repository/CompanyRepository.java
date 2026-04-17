package com.example.profileservice.repository;

import com.example.profileservice.model.Company;
import com.example.profileservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUser(User user);
}