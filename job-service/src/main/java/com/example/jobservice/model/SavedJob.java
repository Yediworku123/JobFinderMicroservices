package com.example.jobservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saved_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJob {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    // CHANGE: Long -> String
    // This aligns with the UUID returned by SecurityUtils.getUserId()
    private String userId;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    private LocalDateTime savedAt;

    @PrePersist
    public void onSave() {
        savedAt = LocalDateTime.now();
    }
}