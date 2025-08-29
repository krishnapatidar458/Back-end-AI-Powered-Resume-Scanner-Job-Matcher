package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerAnalysisDTO {
    private Long userId;
    
    // Current profile analysis
    private Double averageMatchScore;
    private Integer totalMatches;
    private List<String> topSkills;
    private List<String> industryExperience;
    
    // Career path recommendations
    private List<CareerPathSuggestion> suggestedCareerPaths;
    private List<String> skillGaps;
    private List<String> recommendedSkills;
    private List<String> emergingSkills;
    
    // Market insights
    private Map<String, Double> skillDemand;
    private List<String> growingIndustries;
    private String careerStage; // ENTRY, MID, SENIOR, EXECUTIVE
    
    // Personalized recommendations
    private String overallRecommendation;
    private List<String> nextSteps;
    private List<String> learningResources;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareerPathSuggestion {
        private String jobTitle;
        private String industry;
        private Double matchProbability;
        private List<String> requiredSkills;
        private String timeToTransition;
    }
}
