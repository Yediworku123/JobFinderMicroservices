package com.example.jobservice.service;

import com.example.jobservice.client.ProfileClient;
import com.example.jobservice.config.SecurityUtils;
import com.example.jobservice.dto.JobResponse;
import com.example.jobservice.dto.ProviderDTO;
import com.example.jobservice.model.*;
import com.example.jobservice.repository.JobRepository;
import com.example.jobservice.repository.JobSkillRepository;
import com.example.jobservice.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SavedJobRepository savedJobRepository;
    private final ProfileClient profileClient;

    // =========================
    // CREATE JOB
    // =========================
    public JobResponse createJob(Job job) {

        String providerId = SecurityUtils.getUserId();

        if (!SecurityUtils.hasRole("PROVIDER")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only providers can create jobs"
            );
        }

        Long companyId = fetchCompanyId(providerId);

        job.setProviderId(providerId);
        job.setCompanyId(companyId);
        job.setStatus(JobStatus.OPEN);
        job.setApprovalStatus(ApprovalStatus.PENDING);

        Job savedJob = jobRepository.save(job);

        // ✅ FIX: fetch provider instead of null
        ProviderDTO provider = fetchProvider(providerId);

        return toJobResponse(savedJob, provider);
    }

    // =========================
    // GET ALL JOBS
    // =========================
    public List<JobResponse> getAllJobs() {

        Map<String, ProviderDTO> cache = new HashMap<>();

        return jobRepository.findAll()
                .stream()
                .map(job -> {
                    ProviderDTO provider = cache.computeIfAbsent(
                            job.getProviderId(),
                            this::fetchProvider
                    );
                    return toJobResponse(job, provider);
                })
                .toList();
    }

    // =========================
    // GET JOB BY ID
    // =========================
    public JobResponse getJobById(UUID jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found with id: " + jobId
                ));

        return toJobResponse(job, fetchProvider(job.getProviderId()));
    }

    // =========================
    // GET BY PROVIDER
    // =========================
    public List<JobResponse> getJobsByProvider() {

        String providerId = SecurityUtils.getUserId();

        ProviderDTO provider = fetchProvider(providerId);

        return jobRepository.findByProviderId(providerId)
                .stream()
                .map(job -> toJobResponse(job, provider))
                .toList();
    }

    // =========================
    // DELETE JOB
    // =========================
    public void deleteJob(UUID jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found with id: " + jobId
                ));

        String userId = SecurityUtils.getUserId();

        if (!job.getProviderId().equals(userId)
                && !SecurityUtils.hasRole("admin")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not allowed to delete this job"
            );
        }

        jobRepository.delete(job);
    }

    // =========================
    // SAVE JOB
    // =========================
    // =========================
    // SAVE JOB
    // =========================
    public SavedJob saveJob(UUID jobId) {

        String userId = SecurityUtils.getUserId(); // Returns "2033..." (String)

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found with id: " + jobId
                ));

        SavedJob savedJob = SavedJob.builder()
                .userId(userId) // FIXED: No conversion needed, just pass the String
                .job(job)
                .build();

        return savedJobRepository.save(savedJob);
    }

    // ... existing code ...

    public List<SavedJob> getSavedJobs() {

        String userId = SecurityUtils.getUserId();

        return savedJobRepository.findByUserId(userId);
    }

    // =========================
    // ADD SKILLS
    // =========================
    public List<JobSkill> addSkillsToJob(UUID jobId, List<JobSkill> skills) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found with id: " + jobId
                ));

        skills.forEach(skill -> skill.setJob(job));

        return jobSkillRepository.saveAll(skills);
    }

    // =========================
    // FEIGN: PROVIDER
    // =========================
    private ProviderDTO fetchProvider(String providerId) {
        try {
            return profileClient.getProviderById(providerId);
        } catch (Exception e) {
            return null; // safer than breaking response
        }
    }

    // =========================
    // FEIGN: COMPANY
    // =========================
    private Long fetchCompanyId(String providerId) {
        try {
            return profileClient.getCompanyIdByProvider(providerId);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Company fetch failed: " + e.getMessage()
            );
        }
    }

    // =========================
    // MAPPER
    // =========================
    private JobResponse toJobResponse(Job job, ProviderDTO provider) {

        JobResponse response = new JobResponse();

        response.setId(job.getId().toString());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setLocation(job.getLocation());
        response.setType(job.getJobType());

        response.setCompanyId(job.getCompanyId());
        response.setSalaryMin(job.getSalaryMin());
        response.setSalaryMax(job.getSalaryMax());

        response.setStatus(job.getStatus());
        response.setApprovalStatus(job.getApprovalStatus());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());

        response.setSkills(
                job.getSkills() == null
                        ? List.of()
                        : job.getSkills()
                          .stream()
                          .map(JobSkill::getSkillName)
                          .toList()
        );

        response.setProvider(provider);

        return response;
    }

}