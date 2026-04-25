package com.example.jobservice.controller;

import com.example.jobservice.dto.ApiResponse;
import com.example.jobservice.dto.JobResponse;
import com.example.jobservice.dto.JobSkillDTO;
import com.example.jobservice.dto.SavedJobDTO;
import com.example.jobservice.model.Job;
import com.example.jobservice.model.JobSkill;
import com.example.jobservice.model.SavedJob;
import com.example.jobservice.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // =========================
    // CREATE JOB
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody Job job
    ) {
        try {
            // Note: Service returns DTO directly, so we just wrap it
            return ApiResponse.success(jobService.createJob(job), "Job created successfully");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // =========================
    // GET ALL JOBS
    // =========================
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getAllJobs() {
        return ApiResponse.success(jobService.getAllJobs(), "Fetched all jobs");
    }

    // =========================
    // GET JOB BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable UUID id) {
        try {
            return ApiResponse.success(jobService.getJobById(id), "Job found");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // =========================
    // GET MY JOBS (PROVIDER)
    // =========================
    @GetMapping("/my-jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getMyJobs() {
        return ApiResponse.success(jobService.getJobsByProvider(), "Fetched your jobs");
    }

    // =========================
    // SAVE JOB
    // =========================
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<SavedJobDTO>> saveJob(@RequestParam UUID jobId) {
        try {
            // Service returns Entity, we map to DTO
            SavedJob entity = jobService.saveJob(jobId);
            SavedJobDTO dto = mapToSavedJobDTO(entity);
            return ApiResponse.success(dto, "Job saved successfully");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // =========================
    // GET SAVED JOBS
    // =========================
    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<List<SavedJobDTO>>> getSavedJobs() {
        return ApiResponse.success(
                jobService.getSavedJobs()
                        .stream()
                        .map(this::mapToSavedJobDTO)
                        .collect(Collectors.toList()),
                "Fetched saved jobs"
        );
    }

    // =========================
    // DELETE JOB
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable UUID id) {
        try {
            jobService.deleteJob(id);
            return ApiResponse.success(null, "Job deleted successfully");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    // =========================
    // Approve
    // =========================
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<JobResponse>> approveJob(@PathVariable UUID id) {
        try {
            return ApiResponse.success(
                    jobService.approveJob(id),
                    "Job approved successfully"
            );
        } catch (Exception e) {
            // Return 403 if security fails, or 404 if job not found
            return ApiResponse.error(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
    // =========================
    // SEARCH JOBS
    // =========================
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<JobResponse>>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location
    ) {
        return ApiResponse.success(
                jobService.searchJobs(title, location),
                "Fetched jobs matching criteria"
        );
    }
    // =========================
    // ADD SKILLS
    // =========================
    @PostMapping("/{jobId}/skills")
    public ResponseEntity<ApiResponse<List<JobSkillDTO>>> addSkills(
            @PathVariable UUID jobId,
            @RequestBody List<JobSkill> skills
    ) {
        try {
            // Service returns Entity list, we map to DTO list
            List<JobSkill> entities = jobService.addSkillsToJob(jobId, skills);

            List<JobSkillDTO> dtos = entities.stream()
                    .map(entity -> JobSkillDTO.builder()
                            .skillName(entity.getSkillName())
                            .build())
                    .collect(Collectors.toList());

            return ApiResponse.success(dtos, "Skills added successfully");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // =========================
    // MAPPER HELPERS
    // =========================
    private SavedJobDTO mapToSavedJobDTO(SavedJob entity) {
        return SavedJobDTO.builder()
                .id(entity.getId().toString())
                .jobId(entity.getJob() != null ? entity.getJob().getId().toString() : null)
                .savedAt(entity.getSavedAt())
                .build();
    }
}