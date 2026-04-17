package com.example.jobservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SavedJobDTO {

    private Long id;
    private Long userId;
    private Long jobId;
    private LocalDateTime savedAt;

    // Optional: include job info
    private JobResponse job;
}