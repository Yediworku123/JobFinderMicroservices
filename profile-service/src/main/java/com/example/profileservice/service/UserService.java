package com.example.profileservice.service;

import com.example.profileservice.dto.*;
import com.example.profileservice.kafka.KafkaProducer;
import com.example.profileservice.model.*;
import com.example.profileservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SeekerSkillRepository seekerSkillRepository;
    private final SeekerWorkExperienceRepository workExperienceRepository;
    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final CompanyRepository companyRepository;
    private final KafkaProducer kafkaProducer;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String KEYCLOAK_URL = "http://localhost:8280";
    private final String REALM = "jobfindermultiserver";
    private final String CLIENT_ID = "profile-service";
    private final String CLIENT_SECRET = "PhdedLBHjvT05Kue6LWRPwoyV8jKaIyr";

    // =========================
    // KEYCLOAK TOKEN
    // =========================
    private String getAdminToken() {
        String url = KEYCLOAK_URL + "/realms/" + REALM + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials" +
                "&client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public User findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRoleType(role);
    }

    // =========================
    // CREATE KEYCLOAK USER
    // =========================
    private String createUserInKeycloak(RegisterRequest req, String token) {

        String url = KEYCLOAK_URL + "/admin/realms/" + REALM + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = new HashMap<>();
        user.put("username", req.getEmail());
        user.put("email", req.getEmail());
        user.put("enabled", true);
        user.put("firstName", req.getFirstName());
        user.put("lastName", req.getLastName());

        Map<String, Object> cred = new HashMap<>();
        cred.put("type", "password");
        cred.put("value", req.getPassword());
        cred.put("temporary", false);

        user.put("credentials", List.of(cred));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            String location = response.getHeaders().getLocation().toString();
            return location.substring(location.lastIndexOf("/") + 1);

        } catch (HttpClientErrorException.Conflict e) {
            return getUserIdByEmail(req.getEmail(), token);
        }
    }

    private String getUserIdByEmail(String email, String token) {

        String url = KEYCLOAK_URL + "/admin/realms/" + REALM + "/users?email=" + email;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);

        if (!response.getBody().isEmpty()) {
            Map user = (Map) response.getBody().get(0);
            return (String) user.get("id");
        }

        throw new RuntimeException("User exists but cannot retrieve ID");
    }

    private void assignRole(String keycloakId, String roleName, String token) {

        String roleUrl = KEYCLOAK_URL + "/admin/realms/" + REALM + "/roles/" + roleName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                roleUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        String assignUrl = KEYCLOAK_URL + "/admin/realms/" + REALM +
                "/users/" + keycloakId + "/role-mappings/realm";

        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(assignUrl, new HttpEntity<>(List.of(roleResponse.getBody()), headers), Void.class);
    }

    // =========================
    // REGISTER USER
    // =========================
    public void registerUser(RegisterRequest req) {

        String token = getAdminToken();
        String keycloakId = createUserInKeycloak(req, token);

        assignRole(keycloakId, req.getRoleType(), token);

        User user = User.builder()
                .email(req.getEmail())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .roleType(req.getRoleType())
                .keycloakId(keycloakId)
                .build();

        userRepository.save(user);

        if ("SEEKER".equalsIgnoreCase(req.getRoleType())) {

            seekerProfileRepository.save(
                    SeekerProfile.builder()
                            .user(user)
                            .updatedAt(LocalDateTime.now())
                            .build()
            );

            NotificationEvent event = new NotificationEvent();
            event.setType("USER_REGISTERED");
            event.setRecipientId(keycloakId);
            event.setEmail(user.getEmail());
            event.setRole("SEEKER");
            event.setMessage("🎉 Welcome seeker!");

            kafkaProducer.sendEvent(event);
        }

        if ("PROVIDER".equalsIgnoreCase(req.getRoleType())) {

            Company company = companyRepository.save(
                    Company.builder()
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            user.setCompany(company);
            userRepository.save(user);

            NotificationEvent event = new NotificationEvent();
            event.setType("USER_REGISTERED");
            event.setRecipientId(keycloakId);
            event.setEmail(user.getEmail());
            event.setRole("PROVIDER");
            event.setMessage("🏢 Welcome provider!");

            kafkaProducer.sendEvent(event);
        }
    }

    // =========================
    // BUSINESS LOGIC (UNCHANGED)
    // =========================

    public SeekerProfile getSeekerProfile(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return seekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Company getCompanyByProvider(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return companyRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public List<SeekerSkill> addSkills(String keycloakId, List<SingleSkillDTO> skillsDTO) {

        SeekerProfile profile = getSeekerProfile(keycloakId);

        List<SeekerSkill> list = new ArrayList<>();

        for (SingleSkillDTO dto : skillsDTO) {
            list.add(seekerSkillRepository.save(
                    SeekerSkill.builder()
                            .profile(profile)
                            .skillName(dto.getSkillName())
                            .proficiency(dto.getProficiency())
                            .build()
            ));
        }

        return list;
    }

    public SeekerWorkExperience addWorkExperience(String keycloakId, SeekerWorkExperienceDTO dto) {

        SeekerProfile profile = getSeekerProfile(keycloakId);

        return workExperienceRepository.save(
                SeekerWorkExperience.builder()
                        .profile(profile)
                        .companyName(dto.getCompanyName())
                        .jobTitle(dto.getJobTitle())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .description(dto.getDescription())
                        .build()
        );
    }

    public Company updateCompany(String keycloakId, CompanyDTO dto) {

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Company company = companyRepository.findByUser(user)
                .orElseGet(() -> Company.builder()
                        .user(user)
                        .createdAt(LocalDateTime.now())
                        .build());

        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setWebsite(dto.getWebsite());
        company.setLogoUrl(dto.getLogoUrl());
        company.setLocation(dto.getLocation());
        company.setIndustryType(dto.getIndustryType());

        return companyRepository.save(company);
    }
}