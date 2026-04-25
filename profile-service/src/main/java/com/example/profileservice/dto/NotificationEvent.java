package com.example.profileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
public class NotificationEvent {
    private String type;
    private String recipientId;
    private String recipientEmail; // ✅ Standardized name
    private String role;
    private String message;
}