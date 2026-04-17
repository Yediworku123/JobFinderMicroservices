package com.example.applicationservice.service;

import com.example.applicationservice.client.*;
import com.example.applicationservice.dto.*;
import com.example.applicationservice.kafka.KafkaProducer;
import com.example.applicationservice.model.*;
import com.example.applicationservice.repository.*;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ResumeRepository resumeRepository;
    private final KafkaProducer kafkaProducer;

    private final ProfileClient profileClient;
    private final JobServiceClient jobServiceClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    // ✅ Signature change: Jwt jwt -> String userId
    public Application applyForJob(
            String jobId,
            String email,
            String userId,
            MultipartFile resumeFile,
            String coverLetter
    ) throws Exception {

        // 1. GET USER
        ProviderDTO user = profileClient.getUserByEmail(email);
        if (user == null) throw new RuntimeException("User not found");

        // 2. SECURITY CHECK
        if (!user.getKeycloakId().equals(userId)) {
            throw new RuntimeException("You cannot apply using another user's email");
        }

        // 3. ROLE CHECK
        if (!"SEEKER".equalsIgnoreCase(user.getRoleType())) {
            throw new RuntimeException("Only seekers can apply for jobs");
        }

        String seekerId = user.getKeycloakId();

        // 4. GET JOB
        JobResponseDTO job = jobServiceClient.getJobById(jobId);
        if (job == null || job.getProvider() == null) {
            throw new RuntimeException("Job/provider not found");
        }

        String providerId = job.getProvider().getKeycloakId();
        String providerEmail = job.getProvider().getEmail();

        // 5. UPLOAD RESUME
        String fileName = UUID.randomUUID() + "_" + resumeFile.getOriginalFilename();

        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();

        try (InputStream is = resumeFile.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(is, resumeFile.getSize(), -1)
                            .contentType(resumeFile.getContentType())
                            .build()
            );
        }

        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(fileName)
                        .expiry(3600)
                        .build()
        );

        // 6. SAVE RESUME
        Resume resume = resumeRepository.save(
                Resume.builder()
                        .userId(seekerId)
                        .fileUrl(url)
                        .fileName(fileName)
                        .uploadedAt(LocalDateTime.now())
                        .build()
        );

        // 7. SAVE APPLICATION
        Application app = applicationRepository.save(
                Application.builder()
                        .jobId(jobId)
                        .seekerId(seekerId)
                        .resume(resume)
                        .coverLetter(coverLetter)
                        .status("APPLIED")
                        .appliedAt(LocalDateTime.now())
                        .build()
        );

        // 8. NOTIFICATIONS
        NotificationEvent providerEvent = new NotificationEvent();
        providerEvent.setType("APPLICATION_SUBMITTED");
        providerEvent.setRecipientId(providerId);
        providerEvent.setRecipientEmail(providerEmail);
        providerEvent.setRole("PROVIDER");
        providerEvent.setMessage("New application received!");

        kafkaProducer.sendEvent(providerEvent);

        NotificationEvent seekerEvent = new NotificationEvent();
        seekerEvent.setType("APPLICATION_SUBMITTED");
        seekerEvent.setRecipientId(seekerId);
        seekerEvent.setRecipientEmail(user.getEmail());
        seekerEvent.setRole("SEEKER");
        seekerEvent.setMessage("You applied successfully!");

        kafkaProducer.sendEvent(seekerEvent);

        return app;
    }

    public List<Application> getApplicationsForJob(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    // ✅ Signature change: Jwt jwt -> String userId
    public List<Application> getApplicationsByEmail(String email, String userId) {

        ProviderDTO user = profileClient.getUserByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // SECURITY CHECK AGAIN
        if (!user.getKeycloakId().equals(userId)) {
            throw new RuntimeException("You cannot view another user's applications");
        }

        return applicationRepository.findBySeekerId(user.getKeycloakId());
    }
}