package com.example.jobservice.dto;


import com.example.jobservice.model.ApprovalStatus;
import com.example.jobservice.model.JobStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobResponse {

    private String id;
    private String title;
    private String description;
    private String location;
    private String type;

    private Long companyId;
    private CompanyDTO company;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private JobStatus status;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> skills;

    private ProviderDTO provider;
}