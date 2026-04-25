package com.example.applicationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private String id;
    private String jobId;
    private String seekerId;
    private String status; // APPLIED, VIEWED, etc.
    private LocalDateTime appliedAt;

    // We flatten the resume info here to make it easy for the frontend
    private String resumeFileUrl;
    private String resumeFileName;
    private String coverLetter;
}