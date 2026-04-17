package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.NotificationEvent;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        log.info("📥 Received event from Kafka: {}", event);
        notificationService.handleNotification(event);
    }
}