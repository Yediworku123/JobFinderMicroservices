package com.example.applicationservice.client;

import com.example.applicationservice.dto.ApiResponse;
import com.example.applicationservice.dto.JobResponseDTO;
import com.example.applicationservice.config.FeignConfig; // ✅ Import the config
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// ✅ Add configuration = FeignConfig.class here
@FeignClient(name = "job-service", url = "http://localhost:8087", configuration = FeignConfig.class)
public interface JobServiceClient {

    @GetMapping("/jobs/{id}")
    ApiResponse<JobResponseDTO> getJobById(@PathVariable("id") String jobId);
}