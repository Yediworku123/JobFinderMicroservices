package com.example.jobservice.dto;

import lombok.Data;

@Data
    public class CompanyDTO {
        private Long id;
        private String name;
        private String description;
        private String website;
        private String location;
        private String industryType;
    }

