package com.example.applicationservice.controller;

import com.example.applicationservice.client.JobServiceClient;
import com.example.applicationservice.dto.JobResponseDTO;
import com.example.applicationservice.model.Application;
import com.example.applicationservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobServiceClient jobServiceClient;

    // =========================
    // APPLY
    // =========================
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> apply(
            @RequestParam String jobId,
            @RequestParam String email,
            @RequestParam MultipartFile resume,
            @RequestParam(required = false) String coverLetter,
            @RequestHeader("X-User-Id") String userId // ✅ Read from Gateway Header
    ) {
        try {

            if (jobId == null || email == null || resume == null || resume.isEmpty()) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            Application app = applicationService.applyForJob(
                    jobId,
                    email,
                    userId, // ✅ Pass the userId string
                    resume,
                    coverLetter
            );

            return ResponseEntity.ok(app);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    // =========================
    // GET BY JOB
    // =========================
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getByJob(@PathVariable String jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId));
    }

    // =========================
    // GET MY APPLICATIONS
    // =========================
    @GetMapping("/me")
    public ResponseEntity<List<Application>> getMyApplications(
            @RequestParam String email,
            @RequestHeader("X-User-Id") String userId // ✅ Read from Gateway Header
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationsByEmail(email, userId)
        );
    }
}