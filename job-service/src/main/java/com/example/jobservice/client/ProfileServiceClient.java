//package com.example.jobservice.client;
//
//import com.example.profileservice.model.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class ProfileServiceClient {
//
//    private final RestTemplate restTemplate;
//    private final String PROFILE_SERVICE_URL = "http://localhost:8082/profile/role/SEEKER";
//
//    public List<User> getSeekers() {
//        User[] users = restTemplate.getForObject(PROFILE_SERVICE_URL, User[].class);
//        return users != null ? Arrays.asList(users) : List.of();
//    }
//}