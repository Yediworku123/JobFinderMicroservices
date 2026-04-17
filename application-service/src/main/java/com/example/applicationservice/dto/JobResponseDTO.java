package com.example.applicationservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobResponseDTO {
    private String id;
    private String title;
    private String description;
    private String location;
    private String type;
    private String companyName;
    private ProviderDTO provider; // make sure you also create this class
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}