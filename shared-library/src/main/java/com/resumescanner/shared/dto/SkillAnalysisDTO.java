package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillAnalysisDTO {
    private Long resumeId;
    private Long jobId;
    
    // Skill matching analysis
    private List<SkillMatch> skillMatches;
    private List<String> missingCriticalSkills;
    private List<String> transferableSkills;
    private List<String> emergingSkills;
    
    // Market analysis
    private Map<String, Double> skillMarketDemand;
    private Map<String, String> skillProficiencyLevels;
    
    // Recommendations
    private List<String> skillDevelopmentSuggestions;
    private List<String> certificationRecommendations;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillMatch {
        private String skillName;
        private String matchType; // EXACT, SIMILAR, TRANSFERABLE
        private Double confidenceScore;
        private String importance; // CRITICAL, IMPORTANT, NICE_TO_HAVE
    }
}
