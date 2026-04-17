package com.example.jobservice.config; // Note: Ensure this package matches

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class SecurityUtils {

    public static String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        // In the new filter, we stored the User ID in the 'principal' (name)
        return auth.getName();
    }

    public static String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getDetails() == null) return null;

        // We stored the email in the 'details' field inside HeaderAuthFilter
        if (auth.getDetails() instanceof HeaderAuthFilter.CustomUserDetails details) {
            return details.email();
        }
        return null;
    }

    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null &&
                auth.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}