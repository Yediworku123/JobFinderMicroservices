

package com.example.jobservice.client;

import com.example.jobservice.dto.CompanyDTO;
import com.example.jobservice.dto.ProviderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



    @FeignClient(name = "profile-service", url = "http://localhost:8082/profile")
    public interface ProfileClient {

        @GetMapping("/keycloak/{keycloakId}")
        ProviderDTO getProviderById(@PathVariable("keycloakId") String providerId);
        @GetMapping("/provider/{providerId}/company")
        Long getCompanyIdByProvider(@PathVariable String providerId);
    }
