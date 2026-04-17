package com.example.profileservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keycloakId;

    @Column(unique = true, nullable = false)
    private String email;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Company company;

    @Column(nullable = false)
    private String roleType; // ADMIN, SEEKER, PROVIDER

    private String firstName;
    private String lastName;
    private String phone;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}