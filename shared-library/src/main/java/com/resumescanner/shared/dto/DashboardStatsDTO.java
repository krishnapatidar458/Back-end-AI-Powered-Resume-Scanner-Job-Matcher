package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    // User statistics
    private Long totalUsers;
    private Long activeUsers;
    private Long newUsersThisMonth;
    
    // Resume statistics
    private Long totalResumes;
    private Long resumesProcessedToday;
    private Long resumesProcessing;
    private Long resumesFailed;
    
    // Job statistics
    private Long totalJobs;
    private Long activeJobs;
    private Long newJobsThisWeek;
    
    // Matching statistics
    private Long totalMatches;
    private Long matchesToday;
    private Double averageMatchScore;
    
    // System health
    private String systemStatus;
    private Double systemLoadPercentage;
}
