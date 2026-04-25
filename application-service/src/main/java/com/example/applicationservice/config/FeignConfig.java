package com.example.applicationservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 1. Get the headers that the API Gateway set for you
            String userId = request.getHeader("X-User-Id");
            String email = request.getHeader("X-User-Email");
            String roles = request.getHeader("X-Roles");

            // 2. Forward these specific headers to the downstream services
            // Job Service needs "X-User-Id" to work correctly with HeaderAuthFilter
            if (userId != null) {
                template.header("X-User-Id", userId);
            }

            if (email != null) {
                template.header("X-User-Email", email);
            }

            if (roles != null) {
                template.header("X-Roles", roles);
            }

            // Note: We REMOVED the "Authorization" forwarding because
            // your downstream services are configured to trust the Headers,
            // not validate the JWT token again.
        }
    }
}