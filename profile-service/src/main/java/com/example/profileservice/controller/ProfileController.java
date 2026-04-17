package com.example.profileservice.controller;

import com.example.profileservice.dto.*;
import com.example.profileservice.model.*;
import com.example.profileservice.repository.UserRepository;
import com.example.profileservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    // ========================
    // AUTH / USER BASIC INFO
    // ========================

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserDTO> getUserByKeycloakId(@PathVariable String keycloakId) {
        User user = userService.findByKeycloakId(keycloakId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapToDTO(user));
    }

    // ⭐ IMPORTANT: THIS IS WHAT YOUR APPLICATION SERVICE NEEDS
    @GetMapping("/by-email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapToDTO(user));
    }

    // ========================
    // ROLE BASED USERS
    // ========================

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    // ========================
    // SEEKER PROFILE
    // ========================

    @GetMapping("/seeker/{keycloakId}")
    public ResponseEntity<SeekerProfile> getSeekerProfile(@PathVariable String keycloakId) {
        return ResponseEntity.ok(userService.getSeekerProfile(keycloakId));
    }

    @GetMapping("/seeker/{keycloakId}/full")
    public ResponseEntity<SeekerProfile> getFullSeekerProfile(@PathVariable String keycloakId) {
        return ResponseEntity.ok(userService.getSeekerProfile(keycloakId));
    }

    @PostMapping("/seeker/{keycloakId}/skill")
    public ResponseEntity<List<SeekerSkill>> addSkill(
            @PathVariable String keycloakId,
            @RequestBody SeekerSkillDTO dto
    ) {
        return ResponseEntity.ok(userService.addSkills(keycloakId, dto.getSkills()));
    }

    @PostMapping("/seeker/{keycloakId}/work")
    public ResponseEntity<SeekerWorkExperience> addWork(
            @PathVariable String keycloakId,
            @RequestBody SeekerWorkExperienceDTO dto
    ) {
        return ResponseEntity.ok(userService.addWorkExperience(keycloakId, dto));
    }

    // ========================
    // PROVIDER / COMPANY
    // ========================

    @PutMapping("/provider/{keycloakId}/company")
    public ResponseEntity<Company> updateCompany(
            @PathVariable String keycloakId,
            @RequestBody CompanyDTO dto
    ) {
        return ResponseEntity.ok(userService.updateCompany(keycloakId, dto));
    }

    @GetMapping("/provider/{keycloakId}/company")
    public ResponseEntity<Long> getCompanyId(@PathVariable String keycloakId) {
        return ResponseEntity.ok(
                userService.getCompanyByProvider(keycloakId).getId()
        );
    }

    // ========================
    // MAPPER (clean code)
    // ========================

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setKeycloakId(user.getKeycloakId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRoleType(user.getRoleType());
        return dto;
    }
}