package com.example.jobservice.repository;

import com.example.jobservice.model.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobSkillRepository extends JpaRepository<JobSkill, UUID>{
    
}