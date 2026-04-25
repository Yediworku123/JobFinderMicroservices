package com.example.applicationservice.controller;

import com.example.applicationservice.dto.ApiResponse;
import com.example.applicationservice.dto.ApplicationDTO;
import com.example.applicationservice.dto.JobApplicationDTO;
import com.example.applicationservice.model.Application;
import com.example.applicationservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // =========================
    // APPLY (POST)
    // =========================
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicationDTO>> apply(
            // ✅ Use @ModelAttribute to map Form Data to your DTO
            @ModelAttribute JobApplicationDTO dto,
            @RequestHeader("X-User-Id") String userId
    ) {
        try {
            // 1. Basic Validation
            if (dto.getJobId() == null || dto.getEmail() == null || dto.getResume() == null || dto.getResume().isEmpty()) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Missing required fields");
            }

            // 2. Call Service
            Application entity = applicationService.applyForJob(
                    dto.getJobId(),
                    dto.getEmail(),
                    userId,
                    dto.getResume(),
                    dto.getCoverLetter()
            );

            // 3. Map Entity to DTO
            ApplicationDTO response = mapToDTO(entity);

            // 4. Return Wrapped Response
            return ApiResponse.success(response, "Application submitted successfully");

        } catch (Exception e) {
            e.printStackTrace(); // For debugging, use Slf4j logger in production
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }

    // =========================
    // GET BY JOB ID (PROVIDER VIEW)
    // =========================
    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getByJob(@PathVariable String jobId) {
        try {
            List<Application> entities = applicationService.getApplicationsForJob(jobId);

            List<ApplicationDTO> dtoList = entities.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            return ApiResponse.success(dtoList, "Fetched applications for job: " + jobId);

        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // =========================
    // GET MY APPLICATIONS (SEEKER VIEW)
    // =========================
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getMyApplications(
            @RequestParam String email,
            @RequestHeader("X-User-Id") String userId
    ) {
        try {
            List<Application> entities = applicationService.getApplicationsByEmail(email, userId);

            List<ApplicationDTO> dtoList = entities.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            return ApiResponse.success(dtoList, "Fetched your applications");

        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // =========================
    // HELPER MAPPER
    // =========================
    private ApplicationDTO mapToDTO(Application entity) {
        return ApplicationDTO.builder()
                .id(entity.getId())
                .jobId(entity.getJobId())
                .seekerId(entity.getSeekerId())
                .status(entity.getStatus())
                .appliedAt(entity.getAppliedAt())
                .coverLetter(entity.getCoverLetter())
                .resumeFileUrl(entity.getResume() != null ? entity.getResume().getFileUrl() : null)
                .resumeFileName(entity.getResume() != null ? entity.getResume().getFileName() : null)
                .build();
    }
}