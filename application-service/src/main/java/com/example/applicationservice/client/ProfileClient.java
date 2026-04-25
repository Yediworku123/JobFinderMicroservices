package com.example.applicationservice.client;

import com.example.applicationservice.dto.ApiResponse;
import com.example.applicationservice.dto.ProviderDTO;
import com.example.applicationservice.config.FeignConfig; // ✅ Import the config
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ✅ Add configuration = FeignConfig.class here
@FeignClient(name = "profile-service", url = "http://localhost:8082", configuration = FeignConfig.class)
public interface ProfileClient {

    @GetMapping("/profile/by-email")
    ApiResponse<ProviderDTO> getUserByEmail(@RequestParam("email") String email);
}