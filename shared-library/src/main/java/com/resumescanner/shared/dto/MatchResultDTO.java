package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultDTO {
    private Long id;
    private Long userId;
    private Long resumeId;
    private Long jobId;
    
    // Score breakdown
    private Double overallScore;
    private Double keywordMatchScore;
    private Double semanticMatchScore;
    private Double experienceMatchScore;
    private Double skillsMatchScore;
    private Double educationMatchScore;
    private Double atsFriendlyScore;
    
    // Skill analysis
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    
    // AI-generated insights
    private String recommendations;
    private String strengths;
    private String improvements;
    private String careerAdvice;
    
    private LocalDateTime createdAt;
}
