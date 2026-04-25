package com.example.jobservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.Builder;
@Builder
@Data
public class SavedJobDTO {

    private String id;         // ✅ Changed from Long to String (for UUID)
    private String userId;       // ✅ Changed from Long to String (to match Entity)
    private String jobId;        // ✅ Changed from Long to String (for UUID)
    private LocalDateTime savedAt;

    // Optional: include job info
    private JobResponse job;
}