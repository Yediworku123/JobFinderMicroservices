package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationEvent;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List; // ✅ Make sure this import is there

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final EmailService emailService;

    // =========================
    // CONSUME & SEND EMAIL
    // =========================
    public void handleNotification(NotificationEvent event) {

        log.info("📥 Received event: {}", event);

        String subject = "JobFinder Notification";
        String message = event.getMessage();

        switch (event.getType()) {

            case "USER_REGISTERED":
                createNotification(
                        event.getRecipientId(),
                        message,
                        "SYSTEM"
                );
                emailService.sendEmail(event.getEmail(), subject, message);
                break;

            case "JOB_CREATED":
                createNotification(
                        event.getRecipientId(),
                        message,
                        "JOB_UPDATE"
                );
                emailService.sendEmail(event.getEmail(), subject, message);
                break;

            case "APPLICATION_SUBMITTED":
                createNotification(
                        event.getRecipientId(),
                        message,
                        "APPLICATION_UPDATE"
                );
                emailService.sendEmail(event.getEmail(), subject, message);
                break;

            default:
                log.warn("⚠️ Unknown event type: {}", event.getType());
        }
    }

    // =========================
    // FETCH NOTIFICATIONS
    // =========================
    public List<Notification> getNotificationsByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    private void createNotification(String userId, String message, String type) {

        if (userId == null) {
            log.warn("❌ Skipping notification — recipientId is null");
            return;
        }

        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        repository.save(notification);

        log.info("✅ Notification saved for userId={} | message={}", userId, message);
    }
}