package com.example.profileservice.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class SeekerWorkExperienceDTO {
    private String companyName;
    private String jobTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}