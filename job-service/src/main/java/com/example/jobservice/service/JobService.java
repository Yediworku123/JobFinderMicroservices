package com.example.jobservice.service;

import com.example.jobservice.client.ProfileClient;
import com.example.jobservice.config.SecurityUtils;
import com.example.jobservice.dto.ApiResponse;
import com.example.jobservice.dto.JobResponse;
import com.example.jobservice.dto.NotificationEvent;
import com.example.jobservice.dto.ProviderDTO;
import com.example.jobservice.model.*;
import com.example.jobservice.repository.JobRepository;
import com.example.jobservice.repository.JobSkillRepository;
import com.example.jobservice.repository.SavedJobRepository;
import com.example.jobservice.kafka.KafkaProducer;
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
    private final KafkaProducer kafkaProducer;

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

        // Fetch provider details
        ProviderDTO provider = fetchProvider(providerId);

        // ✅ SEND JOB_CREATED EVENT
        if (provider != null) {
            NotificationEvent event = new NotificationEvent();
            event.setType("JOB_CREATED");
            event.setRecipientId(provider.getKeycloakId());
            event.setRecipientEmail(provider.getEmail());
            event.setRole("PROVIDER");
            event.setMessage("Your job '" + job.getTitle() + "' has been created and is Pending Approval.");
            kafkaProducer.sendEvent(event);
        }

        return toJobResponse(savedJob, provider);
    }

    // =========================
    // GET ALL JOBS (SMART FILTER) ✅ UPDATED
    // =========================
    public List<JobResponse> getAllJobs() {

        List<Job> jobs;

        // 1. Check Roles
        boolean isProvider = SecurityUtils.hasRole("PROVIDER");
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        if (isProvider) {
            // PROVIDERS: See only their own jobs
            String providerId = SecurityUtils.getUserId();
            jobs = jobRepository.findByProviderId(providerId);
        } else if (isAdmin) {
            // ADMINS: See everything
            jobs = jobRepository.findAll();
        } else {
            // SEEKERS: See only APPROVED jobs
            jobs = jobRepository.findAll().stream()
                    .filter(j -> j.getApprovalStatus() == ApprovalStatus.APPROVED)
                    .toList();
        }

        Map<String, ProviderDTO> cache = new HashMap<>();

        return jobs.stream()
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
                && !SecurityUtils.hasRole("ADMIN")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not allowed to delete this job"
            );
        }

        jobRepository.delete(job);
    }

    // =========================
    // SEARCH JOBS
    // =========================
    public List<JobResponse> searchJobs(String title, String location) {

        List<Job> jobs;

        if (title != null && !title.isBlank() && location != null && !location.isBlank()) {
            jobs = jobRepository.findByTitleContainingIgnoreCase(title);
        }
        else if (title != null && !title.isBlank()) {
            jobs = jobRepository.findByTitleContainingIgnoreCase(title);
        }
        else if (location != null && !location.isBlank()) {
            jobs = jobRepository.findByLocationIgnoreCase(location);
        }
        else {
            jobs = jobRepository.findAll().stream()
                    .filter(j -> j.getApprovalStatus() == ApprovalStatus.APPROVED)
                    .toList();
        }

        return jobs.stream()
                .map(job -> toJobResponse(job, fetchProvider(job.getProviderId())))
                .toList();
    }

    // =========================
    // SAVE JOB
    // =========================
    public SavedJob saveJob(UUID jobId) {

        String userId = SecurityUtils.getUserId();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found with id: " + jobId
                ));

        SavedJob savedJob = SavedJob.builder()
                .userId(userId)
                .job(job)
                .build();

        return savedJobRepository.save(savedJob);
    }

    // =========================
    // GET SAVED JOBS
    // =========================
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
    // APPROVE JOB (ADMIN)
    // =========================
    public JobResponse approveJob(UUID jobId) {

        if (!SecurityUtils.hasRole("ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admins can approve jobs"
            );
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found with id: " + jobId
                ));

        job.setApprovalStatus(ApprovalStatus.APPROVED);
        jobRepository.save(job);

        ProviderDTO provider = fetchProvider(job.getProviderId());

        if (provider != null) {
            NotificationEvent event = new NotificationEvent();
            event.setType("JOB_APPROVED");
            event.setRecipientId(provider.getKeycloakId());
            event.setRecipientEmail(provider.getEmail());
            event.setRole("PROVIDER");
            event.setMessage("Your job '" + job.getTitle() + "' has been approved!");

            kafkaProducer.sendEvent(event);
        }

        return toJobResponse(job, provider);
    }

    // =========================
    // FEIGN: PROVIDER
    // =========================
    private ProviderDTO fetchProvider(String providerId) {
        try {
            ApiResponse<ProviderDTO> response = profileClient.getProviderById(providerId);
            if (response != null && response.getData() != null) {
                return response.getData();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // =========================
    // FEIGN: COMPANY
    // =========================
    private Long fetchCompanyId(String providerId) {
        try {
            ApiResponse<Long> response = profileClient.getCompanyIdByProvider(providerId);
            if (response != null && response.getData() != null) {
                return response.getData();
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
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