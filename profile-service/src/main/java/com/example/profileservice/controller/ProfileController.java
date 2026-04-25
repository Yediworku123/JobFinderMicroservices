package com.example.profileservice.controller;

import com.example.profileservice.dto.*;
import com.example.profileservice.model.*;
import com.example.profileservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    // ========================
    // AUTH / USER BASIC INFO
    // ========================

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request) {
        try {
            userService.registerUser(request);
            return ApiResponse.success(null, "User registered successfully!");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByKeycloakId(@PathVariable String keycloakId) {
        User user = userService.findByKeycloakId(keycloakId);

        if (user == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "User not found with Keycloak ID: " + keycloakId);
        }

        UserDTO dto = mapToDTO(user);
        return ApiResponse.success(dto, "User found");
    }

    // ⭐ IMPORTANT: THIS IS WHAT YOUR APPLICATION SERVICE NEEDS
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);

        if (user == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "User not found with email: " + email);
        }

        UserDTO dto = mapToDTO(user);
        return ApiResponse.success(dto, "User found");
    }

    // ========================
    // ROLE BASED USERS
    // ========================

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);

        List<UserDTO> dtoList = users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ApiResponse.success(dtoList, "Fetched users for role: " + role);
    }

    // ========================
    // SEEKER PROFILE
    // ========================

    @GetMapping("/seeker/{keycloakId}")
    public ResponseEntity<ApiResponse<SeekerProfileDTO>> getSeekerProfile(@PathVariable String keycloakId) {
        try {
            SeekerProfile entity = userService.getSeekerProfile(keycloakId);
            SeekerProfileDTO dto = mapSeekerProfileToDTO(entity);
            return ApiResponse.success(dto, "Seeker profile found");
        } catch (RuntimeException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/seeker/{keycloakId}/full")
    public ResponseEntity<ApiResponse<SeekerProfileDTO>> getFullSeekerProfile(@PathVariable String keycloakId) {
        // For this example, we use the same mapper as above
        try {
            SeekerProfile entity = userService.getSeekerProfile(keycloakId);
            SeekerProfileDTO dto = mapSeekerProfileToDTO(entity);
            return ApiResponse.success(dto, "Full seeker profile found");
        } catch (RuntimeException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/seeker/{keycloakId}/skill")
    public ResponseEntity<ApiResponse<List<SeekerSkillDTO>>> addSkill(
            @PathVariable String keycloakId,
            @RequestBody SeekerSkillDTO dto
    ) {
        try {
            List<SeekerSkill> entities = userService.addSkills(keycloakId, dto.getSkills());

            // Map Entity List to DTO List
            // Structure: List<SeekerSkillDTO> where each DTO contains a List<SingleSkillDTO>
            List<SeekerSkillDTO> responseDtos = entities.stream()
                    .map(skill -> {
                        SeekerSkillDTO wrapper = new SeekerSkillDTO();
                        SingleSkillDTO single = new SingleSkillDTO();
                        single.setSkillName(skill.getSkillName());
                        single.setProficiency(skill.getProficiency());
                        wrapper.setSkills(List.of(single));
                        return wrapper;
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success(responseDtos, "Skills added successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/seeker/{keycloakId}/work")
    public ResponseEntity<ApiResponse<SeekerWorkExperienceDTO>> addWork(
            @PathVariable String keycloakId,
            @RequestBody SeekerWorkExperienceDTO dto
    ) {
        try {
            SeekerWorkExperience entity = userService.addWorkExperience(keycloakId, dto);
            return ApiResponse.success(dto, "Work experience added");
        } catch (RuntimeException e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // ========================
    // PROVIDER / COMPANY
    // ========================

    @PutMapping("/provider/{keycloakId}/company")
    public ResponseEntity<ApiResponse<CompanyDTO>> updateCompany(
            @PathVariable String keycloakId,
            @RequestBody CompanyDTO dto
    ) {
        try {
            Company entity = userService.updateCompany(keycloakId, dto);
            CompanyDTO responseDto = mapCompanyToDTO(entity);
            return ApiResponse.success(responseDto, "Company updated successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/provider/{keycloakId}/company")
    public ResponseEntity<ApiResponse<Long>> getCompanyId(@PathVariable String keycloakId) {
        try {
            Company company = userService.getCompanyByProvider(keycloakId);
            return ApiResponse.success(company.getId(), "Company ID found");
        } catch (RuntimeException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // ========================
    // MAPPER METHODS
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

    private SeekerProfileDTO mapSeekerProfileToDTO(SeekerProfile profile) {
        SeekerProfileDTO dto = new SeekerProfileDTO();
        dto.setId(profile.getId());
        dto.setEducationLevel(profile.getEducationLevel());
        dto.setUpdatedAt(profile.getUpdatedAt());

        // Map User info if available
        if (profile.getUser() != null) {
            dto.setUserId(profile.getUser().getKeycloakId());
            dto.setEmail(profile.getUser().getEmail());
            dto.setFirstName(profile.getUser().getFirstName());
            dto.setLastName(profile.getUser().getLastName());
        }

        // Map Work Experiences
        dto.setWorkExperiences(profile.getWorkExperiences() == null ? null :
                profile.getWorkExperiences().stream()
                .map(this::mapWorkExperienceToDTO)
                .collect(Collectors.toList()));

        // Map Skills
        dto.setSkills(profile.getSkills() == null ? null :
                profile.getSkills().stream()
                .map(skill -> {
                    SeekerSkillDTO skillDTO = new SeekerSkillDTO();
                    SingleSkillDTO single = new SingleSkillDTO();
                    single.setSkillName(skill.getSkillName());
                    single.setProficiency(skill.getProficiency());
                    skillDTO.setSkills(List.of(single));
                    return skillDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }

    private SeekerWorkExperienceDTO mapWorkExperienceToDTO(SeekerWorkExperience exp) {
        SeekerWorkExperienceDTO dto = new SeekerWorkExperienceDTO();
        dto.setCompanyName(exp.getCompanyName());
        dto.setJobTitle(exp.getJobTitle());
        dto.setStartDate(exp.getStartDate());
        dto.setEndDate(exp.getEndDate());
        dto.setDescription(exp.getDescription());
        return dto;
    }

    private CompanyDTO mapCompanyToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setWebsite(company.getWebsite());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setLocation(company.getLocation());
        dto.setIndustryType(company.getIndustryType());
        return dto;
    }
}