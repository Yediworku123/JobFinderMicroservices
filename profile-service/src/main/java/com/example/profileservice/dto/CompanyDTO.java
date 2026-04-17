package com.example.profileservice.dto;


import lombok.Data;

@Data
public class CompanyDTO {
    private String name;
    private String description;
    private String website;
    private String logoUrl;
    private String location;
    private String industryType;
}