package com.example.applicationservice.dto;

import lombok.Data;

@Data
public class NotificationEvent {

    private String type; // USER_REGISTERED, JOB_CREATED, APPLICATION_SUBMITTED

    private String recipientId;     // who gets notification
    private String recipientEmail;  // email for sending mail

    private String role; // SEEKER or PROVIDER

    private String message; // actual message to show
}