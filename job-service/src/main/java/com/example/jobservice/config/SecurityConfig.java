package com.example.jobservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // 👈 IMPORTANT for @PreAuthorize later
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/jobs/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    // ✅ PUT IT RIGHT HERE (INSIDE THIS CLASS)


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            // ✅ ADD DEBUG LOGGING HERE
            System.out.println("========== JWT DEBUG ==========");
            System.out.println("Full Claims: " + jwt.getClaims());
            System.out.println("==============================");

            // Check Realm Access
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            System.out.println("Realm Access Claim: " + realmAccess);

            if (realmAccess == null) {
                System.out.println("❌ realm_access is NULL! Checking for 'resource_access'...");

                // OPTIONAL: Fallback for Client Roles (if you assigned roles to a Client in Keycloak)
                Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
                if (resourceAccess != null) {
                    // You might need to adjust logic here if you use client roles
                    System.out.println("Resource Access found: " + resourceAccess);
                }

                return List.<GrantedAuthority>of();
            }

            Object rolesObj = realmAccess.get("roles");
            System.out.println("Roles Object: " + rolesObj);

            if (rolesObj instanceof List<?> roles) {
                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> {
                            String auth = "ROLE_" + role;
                            System.out.println("Granting Authority: " + auth);
                            return new SimpleGrantedAuthority(auth);
                        })
                        .collect(java.util.stream.Collectors.toList());

                return authorities;
            }

            return List.<GrantedAuthority>of();
        });

        return converter;
    }
}