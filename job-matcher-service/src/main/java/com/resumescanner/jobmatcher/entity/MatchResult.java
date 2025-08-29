package com.resumescanner.jobmatcher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "match_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "resume_id", nullable = false)
    private Long resumeId;
    
    @Column(name = "job_id", nullable = false)
    private Long jobId;
    
    @Column(name = "overall_score")
    private Double overallScore;
    
    @Column(name = "keyword_match_score")
    private Double keywordMatchScore;
    
    @Column(name = "semantic_match_score")
    private Double semanticMatchScore;
    
    @Column(name = "experience_match_score")
    private Double experienceMatchScore;
    
    @Column(name = "skills_match_score")
    private Double skillsMatchScore;
    
    @Column(name = "education_match_score")
    private Double educationMatchScore;
    
    @ElementCollection
    @CollectionTable(name = "match_result_matched_skills", joinColumns = @JoinColumn(name = "match_result_id"))
    @Column(name = "skill")
    private List<String> matchedSkills = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "match_result_missing_skills", joinColumns = @JoinColumn(name = "match_result_id"))
    @Column(name = "skill")
    private List<String> missingSkills = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "match_result_matched_keywords", joinColumns = @JoinColumn(name = "match_result_id"))
    @Column(name = "keyword")
    private List<String> matchedKeywords = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "match_result_missing_keywords", joinColumns = @JoinColumn(name = "match_result_id"))
    @Column(name = "keyword")
    private List<String> missingKeywords = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String recommendations;
    
    @Column(columnDefinition = "TEXT")
    private String strengths;
    
    @Column(columnDefinition = "TEXT")
    private String improvements;
    
    @Column(name = "ats_friendly_score")
    private Double atsFriendlyScore;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
