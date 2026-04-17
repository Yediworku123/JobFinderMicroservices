package com.example.profileservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SeekerProfileDTO {

    private Long id;
    private String userId; // keycloakId or user ID
    private String email; // optional
    private String firstName; // optional
    private String lastName; // optional
    private String educationLevel;
    private LocalDateTime updatedAt;

    private List<SeekerSkillDTO> skills;
    private List<SeekerWorkExperienceDTO> workExperiences;

}