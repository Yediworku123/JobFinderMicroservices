package com.example.profileservice.kafka;

import com.example.profileservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void sendEvent(NotificationEvent event) {
        log.info("📤 Sending event to Kafka: {}", event);
        kafkaTemplate.send("notification-topic", event);
    }
}