package com.example.jobservice.client;

import com.example.jobservice.dto.ApiResponse; // ✅ Import this
import com.example.jobservice.dto.ProviderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "http://localhost:8082/profile")
public interface ProfileClient {

    // ✅ CHANGE RETURN TYPE HERE
    @GetMapping("/keycloak/{keycloakId}")
    ApiResponse<ProviderDTO> getProviderById(@PathVariable("keycloakId") String providerId);

    @GetMapping("/provider/{providerId}/company")
    ApiResponse<Long> getCompanyIdByProvider(@PathVariable String providerId);
}