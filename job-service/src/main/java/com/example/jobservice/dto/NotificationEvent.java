package com.example.jobservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // ✅ ADD THIS
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String type;
    private String recipientId;
    private String recipientEmail;
    private String role;
    private String message;
}