package com.example.applicationservice.dto;

import lombok.Data;

@Data
public class ProviderDTO {
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String roleType;
}