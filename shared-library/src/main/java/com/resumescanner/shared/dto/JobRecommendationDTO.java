package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRecommendationDTO {
    private JobDTO job;
    private Double matchScore;
    private String matchReason;
    private List<String> matchingSkills;
    private List<String> skillGaps;
    private String recommendationPriority; // HIGH, MEDIUM, LOW
    private String applicationAdvice;
}
