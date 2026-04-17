package com.example.jobservice.dto;

import lombok.Data;

@Data
public class NotificationEvent {

    private String type;

    private String recipientId;
    private String recipientEmail;

    private String role;

    private String message;
}