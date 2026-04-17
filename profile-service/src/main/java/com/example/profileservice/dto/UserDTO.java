package com.example.profileservice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String roleType; // PROVIDER / SEEKER / ADMIN
}