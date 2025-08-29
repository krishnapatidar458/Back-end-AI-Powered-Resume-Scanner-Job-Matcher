package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeParsingResultDTO {
    private Long resumeId;
    private String parsingStatus; // SUCCESS, PARTIAL, FAILED
    private Double confidenceScore;
    
    // Extracted personal information
    private PersonalInfoDTO personalInfo;
    
    // Parsed sections
    private List<ExperienceDTO> extractedExperiences;
    private List<EducationDTO> extractedEducation;
    private List<String> extractedSkills;
    private List<String> extractedCertifications;
    private List<String> extractedLanguages;
    
    // Parsing metadata
    private Map<String, Double> sectionConfidenceScores;
    private List<String> parsingWarnings;
    private List<String> parsingErrors;
    private String rawTextPreview;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalInfoDTO {
        private String fullName;
        private String email;
        private String phone;
        private String address;
        private String linkedInUrl;
        private String githubUrl;
        private String portfolioUrl;
    }
}
