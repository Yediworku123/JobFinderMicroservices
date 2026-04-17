package com.example.profileservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seeker_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private SeekerProfile profile;

    private String skillName;
    private String proficiency;
}