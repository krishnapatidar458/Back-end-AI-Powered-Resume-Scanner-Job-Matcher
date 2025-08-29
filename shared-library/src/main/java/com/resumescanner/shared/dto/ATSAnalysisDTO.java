package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ATSAnalysisDTO {
    private Long resumeId;
    private Long jobId;
    
    // ATS Score breakdown
    private Double overallATSScore;
    private Double keywordDensityScore;
    private Double formatScore;
    private Double sectionScore;
    
    // Keyword analysis
    private List<String> foundKeywords;
    private List<String> missingKeywords;
    private Map<String, Integer> keywordFrequency;
    
    // ATS Recommendations
    private List<String> atsRecommendations;
    private List<String> formatImprovements;
    private List<String> contentSuggestions;
    
    // Section analysis
    private Boolean hasContactInfo;
    private Boolean hasSummary;
    private Boolean hasWorkExperience;
    private Boolean hasEducation;
    private Boolean hasSkills;
    
    private String atsCompatibilityLevel; // EXCELLENT, GOOD, FAIR, POOR
}
