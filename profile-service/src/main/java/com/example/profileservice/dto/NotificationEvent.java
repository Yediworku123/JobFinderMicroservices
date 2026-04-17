package com.example.profileservice.dto;

import lombok.Data;

@Data
public class NotificationEvent {
    private String type;
    private String recipientId;
    private String email;
    private String role;
    private String message;
}