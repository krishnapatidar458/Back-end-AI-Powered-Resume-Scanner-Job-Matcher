package com.resumescanner.jobmatcher.controller;

import com.resumescanner.jobmatcher.service.JobService;
import com.resumescanner.jobmatcher.service.JobMatcherService;
import com.resumescanner.shared.dto.ApiResponse;
import com.resumescanner.shared.dto.JobDTO;
import com.resumescanner.shared.dto.MatchResultDTO;
import com.resumescanner.shared.enums.JobType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/job-matcher")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Job Matcher", description = "APIs for job matching, ATS simulation, and semantic analysis")
public class JobMatcherController {

    private final JobService jobService;
    private final JobMatcherService jobMatcherService;

    // Job Management Endpoints
    @PostMapping("/jobs")
    @Operation(summary = "Create a new job", description = "Post a new job listing")
    public ResponseEntity<ApiResponse<JobDTO>> createJob(@Valid @RequestBody JobDTO jobDTO) {
        try {
            JobDTO createdJob = jobService.createJob(jobDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Job created successfully", createdJob));
        } catch (Exception e) {
            log.error("Error creating job", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create job"));
        }
    }

    @GetMapping("/jobs/{id}")
    @Operation(summary = "Get job by ID", description = "Retrieve a specific job by its ID")
    public ResponseEntity<ApiResponse<JobDTO>> getJobById(
            @Parameter(description = "Job ID") @PathVariable Long id) {
        try {
            JobDTO job = jobService.getJobById(id);
            return ResponseEntity.ok(ApiResponse.success("Job found", job));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving job by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve job"));
        }
    }

    @GetMapping("/jobs")
    @Operation(summary = "Get all jobs", description = "Retrieve all active job listings with pagination")
    public ResponseEntity<ApiResponse<Page<JobDTO>>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<JobDTO> jobs = jobService.getAllJobs(pageable);
            return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobs));
        } catch (Exception e) {
            log.error("Error retrieving jobs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve jobs"));
        }
    }

    @GetMapping("/jobs/search")
    @Operation(summary = "Search jobs", description = "Search jobs using keywords")
    public ResponseEntity<ApiResponse<Page<JobDTO>>> searchJobs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<JobDTO> jobs = jobService.searchJobs(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success("Search completed successfully", jobs));
        } catch (Exception e) {
            log.error("Error searching jobs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search jobs"));
        }
    }

    @GetMapping("/jobs/advanced-search")
    @Operation(summary = "Advanced job search", description = "Search jobs with multiple filters")
    public ResponseEntity<ApiResponse<Page<JobDTO>>> searchJobsAdvanced(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) Boolean remoteAllowed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<JobDTO> jobs = jobService.searchJobsAdvanced(title, location, companyName, jobType, remoteAllowed, pageable);
            return ResponseEntity.ok(ApiResponse.success("Advanced search completed successfully", jobs));
        } catch (Exception e) {
            log.error("Error in advanced job search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to perform advanced search"));
        }
    }

    // Core Matching Functionality
    @PostMapping("/match")
    @Operation(summary = "Calculate resume-job match", description = "Calculate comprehensive match score between resume and job")
    public ResponseEntity<ApiResponse<MatchResultDTO>> calculateMatch(
            @RequestParam Long resumeId,
            @RequestParam Long jobId,
            @RequestParam Long userId) {
        try {
            MatchResultDTO matchResult = jobMatcherService.calculateMatch(resumeId, jobId, userId);
            return ResponseEntity.ok(ApiResponse.success("Match calculated successfully", matchResult));
        } catch (Exception e) {
            log.error("Error calculating match for resume: {} and job: {}", resumeId, jobId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to calculate match"));
        }
    }

    // Match History and Results
    @GetMapping("/matches/user/{userId}")
    @Operation(summary = "Get user match history", description = "Retrieve match history for a specific user")
    public ResponseEntity<ApiResponse<List<MatchResultDTO>>> getUserMatchHistory(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        try {
            List<MatchResultDTO> matches = jobMatcherService.getUserMatchHistory(userId);
            return ResponseEntity.ok(ApiResponse.success("Match history retrieved successfully", matches));
        } catch (Exception e) {
            log.error("Error retrieving match history for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve match history"));
        }
    }

    @GetMapping("/matches/{matchId}")
    @Operation(summary = "Get match result by ID", description = "Retrieve detailed match result by ID")
    public ResponseEntity<ApiResponse<MatchResultDTO>> getMatchResult(
            @Parameter(description = "Match ID") @PathVariable Long matchId) {
        try {
            MatchResultDTO matchResult = jobMatcherService.getMatchResult(matchId);
            return ResponseEntity.ok(ApiResponse.success("Match result found", matchResult));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving match result: {}", matchId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve match result"));
        }
    }

    // Job Recommendations
    @GetMapping("/recommendations/{userId}")
    @Operation(summary = "Get job recommendations", description = "Get personalized job recommendations based on user's resumes")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobRecommendations(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<JobDTO> recommendations = jobMatcherService.getJobRecommendations(userId, limit);
            return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved successfully", recommendations));
        } catch (Exception e) {
            log.error("Error getting job recommendations for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get recommendations"));
        }
    }

    // Career Path Analysis
    @GetMapping("/career-analysis/{userId}")
    @Operation(summary = "Career path analysis", description = "Analyze career path and suggest skill improvements")
    public ResponseEntity<ApiResponse<Object>> performCareerAnalysis(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        try {
            Object careerAnalysis = jobMatcherService.performCareerAnalysis(userId);
            return ResponseEntity.ok(ApiResponse.success("Career analysis completed successfully", careerAnalysis));
        } catch (Exception e) {
            log.error("Error performing career analysis for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to perform career analysis"));
        }
    }

    @PutMapping("/jobs/{id}")
    @Operation(summary = "Update job", description = "Update an existing job listing")
    public ResponseEntity<ApiResponse<JobDTO>> updateJob(
            @Parameter(description = "Job ID") @PathVariable Long id,
            @Valid @RequestBody JobDTO jobDTO) {
        try {
            JobDTO updatedJob = jobService.updateJob(id, jobDTO);
            return ResponseEntity.ok(ApiResponse.success("Job updated successfully", updatedJob));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating job: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update job"));
        }
    }

    @DeleteMapping("/jobs/{id}")
    @Operation(summary = "Delete job", description = "Delete a job listing")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @Parameter(description = "Job ID") @PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.ok(ApiResponse.success("Job deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting job: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete job"));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the job matcher service is healthy")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Job Matcher Service is healthy", "OK"));
    }
}
