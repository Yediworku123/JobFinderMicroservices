package com.example.jobservice.kafka;

import com.example.jobservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    private static final String TOPIC = "notifications"; // ✅ SAME AS APPLICATION SERVICE

    public void sendEvent(NotificationEvent event) {
        kafkaTemplate.send(TOPIC, event.getRecipientId(), event);
        log.info("📨 Job notification sent: {}", event);
    }
}