package com.example.applicationservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDTO {
    private Long id;
    private String fileUrl;
    private String fileName;
}