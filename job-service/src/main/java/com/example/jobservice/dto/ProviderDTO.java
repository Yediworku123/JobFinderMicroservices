package com.example.jobservice.dto;

import lombok.Data;

@Data
public class ProviderDTO {
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;

    // ✅ REMOVE @JsonProperty — this is the bug
    private String roleType;


    public boolean isProvider() {
        return roleType != null && roleType.equalsIgnoreCase("PROVIDER");
    }
}