package com.example.profileservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "seeker_work_experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekerWorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private SeekerProfile profile;

    private String companyName;
    private String jobTitle;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String description;
}