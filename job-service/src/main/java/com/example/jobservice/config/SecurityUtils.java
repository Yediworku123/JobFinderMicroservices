package com.example.jobservice.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class SecurityUtils {

    public static String getUserId() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("No JWT authentication found");
        }

        Jwt jwt = jwtAuth.getToken();

        return jwt.getSubject(); // user id from Keycloak
    }

    public static String getEmail() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("No JWT authentication found");
        }

        Jwt jwt = jwtAuth.getToken();

        return jwt.getClaimAsString("email");
    }

    public static boolean hasRole(String role) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null &&
                auth.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}