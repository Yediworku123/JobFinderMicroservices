package com.example.jobservice.repository;

import com.example.jobservice.model.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SavedJobRepository extends JpaRepository<SavedJob, UUID> {

    // ✅ CORRECT: Parameter matches the Entity type (String)
    //List<SavedJob> findByUserId(Long userId);
    List<SavedJob> findByUserId(String userId);

}