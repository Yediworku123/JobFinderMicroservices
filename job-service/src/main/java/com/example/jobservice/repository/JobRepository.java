package com.example.jobservice.repository;

import com.example.jobservice.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByProviderId(String providerId);
    List<Job> findByTitleContainingIgnoreCase(String title);
    List<Job> findByLocationIgnoreCase(String location);
}