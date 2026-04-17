//package com.example.jobservice.service;
//
//import com.example.jobservice.client.ProfileServiceClient;
//import com.example.jobservice.kafka.JobKafkaProducer;
//import com.example.profileservice.model.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class JobNotificationService {
//
//    private final ProfileServiceClient profileServiceClient;
//    private final JobKafkaProducer jobKafkaProducer;
//
//    public void notifySeekersJobCreated() {
//        // Fetch all seekers
//        List<User> seekers = profileServiceClient.getSeekers();
//
//        // Send Kafka event to each seeker
//        for (User seeker : seekers) {
//            jobKafkaProducer.sendJobCreatedEvent(seeker.getKeycloakId(), seeker.getEmail());
//        }
//    }
//}