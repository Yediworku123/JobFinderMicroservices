package com.example.jobservice.controller;

import com.example.jobservice.dto.JobResponse;
import com.example.jobservice.model.Job;
import com.example.jobservice.model.JobSkill;
import com.example.jobservice.model.SavedJob;
import com.example.jobservice.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // =========================
    // CREATE JOB
    // =========================
    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody Job job
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(jobService.createJob(job));
    }

    // =========================
    // GET ALL JOBS
    // =========================
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // =========================
    // GET JOB BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // =========================
    // GET MY JOBS (PROVIDER)
    // =========================
    @GetMapping("/my-jobs")
    public ResponseEntity<List<JobResponse>> getMyJobs() {
        return ResponseEntity.ok(jobService.getJobsByProvider());
    }

    // =========================
    // SAVE JOB
    // =========================
    @PostMapping("/save")
    public ResponseEntity<SavedJob> saveJob(@RequestParam UUID jobId) {
        return ResponseEntity.ok(jobService.saveJob(jobId));
    }

    // =========================
    // GET SAVED JOBS
    // =========================
    @GetMapping("/saved")
    public ResponseEntity<List<SavedJob>> getSavedJobs() {
        return ResponseEntity.ok(jobService.getSavedJobs());
    }

    // =========================
    // DELETE JOB
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // ADD SKILLS
    // =========================
    @PostMapping("/{jobId}/skills")
    public ResponseEntity<List<JobSkill>> addSkills(
            @PathVariable UUID jobId,
            @RequestBody List<JobSkill> skills
    ) {
        return ResponseEntity.ok(jobService.addSkillsToJob(jobId, skills));
    }
}