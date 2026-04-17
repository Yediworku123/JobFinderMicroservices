package com.example.applicationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDTO {
    private String jobId;
    private String email;
    private MultipartFile resume;
    private String coverLetter;
}