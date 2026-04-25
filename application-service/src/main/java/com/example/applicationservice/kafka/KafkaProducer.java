package com.example.applicationservice.kafka; // change package per service

import com.example.applicationservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    private static final String TOPIC = "notification-topic";

    public void sendEvent(NotificationEvent event) {
        kafkaTemplate.send(TOPIC, event.getRecipientId(), event);
        log.info("📨 Notification sent to {} : {}", event.getRecipientId(), event.getType());
    }
}