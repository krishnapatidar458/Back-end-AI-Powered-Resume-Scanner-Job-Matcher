package com.resumescanner.jobmatcher.service;

import com.resumescanner.jobmatcher.client.ResumeParserClient;
import com.resumescanner.jobmatcher.entity.Job;
import com.resumescanner.jobmatcher.entity.MatchResult;
import com.resumescanner.jobmatcher.repository.JobRepository;
import com.resumescanner.jobmatcher.repository.MatchResultRepository;
import com.resumescanner.shared.dto.JobDTO;
import com.resumescanner.shared.dto.MatchResultDTO;
import com.resumescanner.shared.dto.ResumeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobMatcherService {

    private final JobRepository jobRepository;
    private final MatchResultRepository matchResultRepository;
    private final ResumeParserClient resumeParserClient;
    private final JobService jobService;

    @Transactional
    public MatchResultDTO calculateMatch(Long resumeId, Long jobId, Long userId) {
        log.info("Calculating match for resume {} and job {} for user {}", resumeId, jobId, userId);

        // Get resume data from resume-parser-service
        ResumeDTO resume = getResumeFromService(resumeId);
        
        // Get job data
        JobDTO job = jobService.getJobById(jobId);

        // Calculate various match scores
        double keywordScore = calculateKeywordMatchScore(resume, job);
        double skillsScore = calculateSkillsMatchScore(resume, job);
        double semanticScore = calculateSemanticMatchScore(resume, job);
        double experienceScore = calculateExperienceMatchScore(resume, job);
        double educationScore = calculateEducationMatchScore(resume, job);

        // Calculate overall score (weighted average)
        double overallScore = calculateOverallScore(keywordScore, skillsScore, semanticScore, experienceScore, educationScore);

        // Generate ATS friendly score
        double atsScore = calculateATSScore(resume, job);

        // Create match result
        MatchResult matchResult = new MatchResult();
        matchResult.setUserId(userId);
        matchResult.setResumeId(resumeId);
        matchResult.setJobId(jobId);
        matchResult.setOverallScore(overallScore);
        matchResult.setKeywordMatchScore(keywordScore);
        matchResult.setSemanticMatchScore(semanticScore);
        matchResult.setExperienceMatchScore(experienceScore);
        matchResult.setSkillsMatchScore(skillsScore);
        matchResult.setEducationMatchScore(educationScore);
        matchResult.setAtsFriendlyScore(atsScore);

        // Analyze skills match
        analyzeSkillsMatch(resume, job, matchResult);

        // Generate recommendations
        generateRecommendations(resume, job, matchResult);

        // Save match result
        MatchResult savedResult = matchResultRepository.save(matchResult);
        log.info("Match result saved with ID: {} and overall score: {}", savedResult.getId(), overallScore);

        return convertToDTO(savedResult);
    }

    public List<MatchResultDTO> getUserMatchHistory(Long userId) {
        return matchResultRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MatchResultDTO getMatchResult(Long matchId) {
        MatchResult matchResult = matchResultRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match result not found with id: " + matchId));
        return convertToDTO(matchResult);
    }

    public List<JobDTO> getJobRecommendations(Long userId, int limit) {
        // Simple recommendation based on active jobs
        return jobService.getActiveJobs().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Object performCareerAnalysis(Long userId) {
        // Basic career analysis - can be expanded with ML algorithms
        Map<String, Object> analysis = new HashMap<>();
        
        List<MatchResult> userMatches = matchResultRepository.findByUserId(userId);
        
        if (!userMatches.isEmpty()) {
            double avgScore = userMatches.stream()
                    .mapToDouble(MatchResult::getOverallScore)
                    .average()
                    .orElse(0.0);
            
            analysis.put("averageMatchScore", avgScore);
            analysis.put("totalMatches", userMatches.size());
            analysis.put("recommendation", generateCareerRecommendation(avgScore));
        } else {
            analysis.put("message", "No match history found for career analysis");
        }
        
        return analysis;
    }

    private ResumeDTO getResumeFromService(Long resumeId) {
        try {
            // In a real implementation, you would parse the response properly
            // For now, creating a mock resume
            ResumeDTO resume = new ResumeDTO();
            resume.setId(resumeId);
            return resume;
        } catch (Exception e) {
            log.error("Error fetching resume from service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch resume data");
        }
    }

    private double calculateKeywordMatchScore(ResumeDTO resume, JobDTO job) {
        if (resume.getRawText() == null || job.getDescription() == null) {
            return 0.0;
        }

        Set<String> resumeWords = extractKeywords(resume.getRawText().toLowerCase());
        Set<String> jobWords = extractKeywords(job.getDescription().toLowerCase());

        JaccardSimilarity jaccard = new JaccardSimilarity();
        return jaccard.apply(String.join(" ", resumeWords), String.join(" ", jobWords)) * 100;
    }

    private double calculateSkillsMatchScore(ResumeDTO resume, JobDTO job) {
        if (resume.getSkills() == null || job.getRequiredSkills() == null) {
            return 0.0;
        }

        Set<String> resumeSkills = resume.getSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        Set<String> jobSkills = job.getRequiredSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<String> intersection = new HashSet<>(resumeSkills);
        intersection.retainAll(jobSkills);

        return jobSkills.isEmpty() ? 0.0 : (double) intersection.size() / jobSkills.size() * 100;
    }

    private double calculateSemanticMatchScore(ResumeDTO resume, JobDTO job) {
        // Basic semantic matching - can be enhanced with NLP libraries
        return calculateKeywordMatchScore(resume, job) * 0.8; // Simplified approach
    }

    private double calculateExperienceMatchScore(ResumeDTO resume, JobDTO job) {
        // Simple experience matching based on years
        if (resume.getWorkExperiences() == null || job.getMinExperienceYears() == null) {
            return 50.0; // Default score
        }

        int totalExperience = resume.getWorkExperiences().size(); // Simplified calculation
        int requiredExperience = job.getMinExperienceYears();

        if (totalExperience >= requiredExperience) {
            return 100.0;
        } else {
            return (double) totalExperience / requiredExperience * 100;
        }
    }

    private double calculateEducationMatchScore(ResumeDTO resume, JobDTO job) {
        // Basic education matching
        return resume.getEducations() != null && !resume.getEducations().isEmpty() ? 80.0 : 40.0;
    }

    private double calculateOverallScore(double keyword, double skills, double semantic, double experience, double education) {
        // Weighted average
        return (keyword * 0.25) + (skills * 0.35) + (semantic * 0.15) + (experience * 0.15) + (education * 0.10);
    }

    private double calculateATSScore(ResumeDTO resume, JobDTO job) {
        // ATS-friendly score based on keyword density and formatting
        return calculateKeywordMatchScore(resume, job) * 0.9; // Simplified
    }

    private void analyzeSkillsMatch(ResumeDTO resume, JobDTO job, MatchResult matchResult) {
        if (resume.getSkills() == null || job.getRequiredSkills() == null) {
            return;
        }

        Set<String> resumeSkills = resume.getSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        Set<String> jobSkills = job.getRequiredSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // Matched skills
        Set<String> matched = new HashSet<>(resumeSkills);
        matched.retainAll(jobSkills);
        matchResult.setMatchedSkills(new ArrayList<>(matched));

        // Missing skills
        Set<String> missing = new HashSet<>(jobSkills);
        missing.removeAll(resumeSkills);
        matchResult.setMissingSkills(new ArrayList<>(missing));
    }

    private void generateRecommendations(ResumeDTO resume, JobDTO job, MatchResult matchResult) {
        StringBuilder recommendations = new StringBuilder();
        StringBuilder strengths = new StringBuilder();
        StringBuilder improvements = new StringBuilder();

        if (matchResult.getOverallScore() >= 80) {
            strengths.append("Excellent match! Your profile aligns well with the job requirements.");
            recommendations.append("Consider applying to this position as you meet most criteria.");
        } else if (matchResult.getOverallScore() >= 60) {
            strengths.append("Good match with some areas for improvement.");
            recommendations.append("Consider strengthening missing skills before applying.");
        } else {
            improvements.append("Significant skill gap identified. ");
            recommendations.append("Focus on developing key skills: ");
            if (matchResult.getMissingSkills() != null && !matchResult.getMissingSkills().isEmpty()) {
                recommendations.append(String.join(", ", matchResult.getMissingSkills().subList(0, Math.min(3, matchResult.getMissingSkills().size()))));
            }
        }

        matchResult.setRecommendations(recommendations.toString());
        matchResult.setStrengths(strengths.toString());
        matchResult.setImprovements(improvements.toString());
    }

    private String generateCareerRecommendation(double avgScore) {
        if (avgScore >= 80) {
            return "You have excellent matching scores! Consider applying to senior positions.";
        } else if (avgScore >= 60) {
            return "Good matching potential. Focus on skill development for better matches.";
        } else {
            return "Consider upskilling in high-demand areas to improve your match scores.";
        }
    }

    private Set<String> extractKeywords(String text) {
        return Arrays.stream(text.split("\\W+"))
                .filter(word -> word.length() > 3)
                .collect(Collectors.toSet());
    }

    private MatchResultDTO convertToDTO(MatchResult matchResult) {
        MatchResultDTO dto = new MatchResultDTO();
        dto.setId(matchResult.getId());
        dto.setUserId(matchResult.getUserId());
        dto.setResumeId(matchResult.getResumeId());
        dto.setJobId(matchResult.getJobId());
        dto.setOverallScore(matchResult.getOverallScore());
        dto.setKeywordMatchScore(matchResult.getKeywordMatchScore());
        dto.setSemanticMatchScore(matchResult.getSemanticMatchScore());
        dto.setExperienceMatchScore(matchResult.getExperienceMatchScore());
        dto.setSkillsMatchScore(matchResult.getSkillsMatchScore());
        dto.setEducationMatchScore(matchResult.getEducationMatchScore());
        dto.setMatchedSkills(matchResult.getMatchedSkills());
        dto.setMissingSkills(matchResult.getMissingSkills());
        dto.setMatchedKeywords(matchResult.getMatchedKeywords());
        dto.setMissingKeywords(matchResult.getMissingKeywords());
        dto.setRecommendations(matchResult.getRecommendations());
        dto.setStrengths(matchResult.getStrengths());
        dto.setImprovements(matchResult.getImprovements());
        dto.setAtsFriendlyScore(matchResult.getAtsFriendlyScore());
        dto.setCreatedAt(matchResult.getCreatedAt());
        return dto;
    }
}
