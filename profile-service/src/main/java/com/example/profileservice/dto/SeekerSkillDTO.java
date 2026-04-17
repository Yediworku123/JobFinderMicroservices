package com.example.profileservice.dto;


import lombok.Data;

import java.util.List;

@Data
public class SeekerSkillDTO {
    private List<SingleSkillDTO> skills;
}