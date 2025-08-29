package com.resumescanner.resumeparser.controller;

import com.resumescanner.resumeparser.service.ResumeParserService;
import com.resumescanner.shared.dto.ApiResponse;
import com.resumescanner.shared.dto.ResumeDTO;
import com.resumescanner.shared.enums.ResumeStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resume-parser")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Resume Parser", description = "APIs for resume upload, parsing, and management")
public class ResumeParserController {

    private final ResumeParserService resumeParserService;

    @PostMapping("/upload")
    @Operation(summary = "Upload and parse a resume", description = "Upload a PDF or DOCX resume file for parsing")
    public ResponseEntity<ApiResponse<ResumeDTO>> uploadResume(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Resume file (PDF or DOCX)") @RequestParam("file") MultipartFile file) {
        try {
            ResumeDTO resume = resumeParserService.uploadResume(userId, file);
            return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", resume));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid file: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading resume for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload resume"));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resume by ID", description = "Retrieve a specific resume by its ID")
    public ResponseEntity<ApiResponse<ResumeDTO>> getResumeById(
            @Parameter(description = "Resume ID") @PathVariable Long id) {
        return resumeParserService.getResumeById(id)
                .map(resume -> ResponseEntity.ok(ApiResponse.success("Resume found", resume)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get resumes by user ID", description = "Retrieve all resumes for a specific user")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getResumesByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<ResumeDTO> resumes = resumeParserService.getResumesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Resumes retrieved successfully", resumes));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get resumes by status", description = "Retrieve resumes filtered by processing status")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getResumesByStatus(
            @Parameter(description = "Resume status") @PathVariable String status) {
        try {
            ResumeStatus resumeStatus = ResumeStatus.valueOf(status.toUpperCase());
            List<ResumeDTO> resumes = resumeParserService.getResumesByStatus(resumeStatus);
            return ResponseEntity.ok(ApiResponse.success("Resumes retrieved successfully", resumes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid status: " + status));
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all resumes", description = "Retrieve all resumes in the system")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getAllResumes() {
        List<ResumeDTO> resumes = resumeParserService.getAllResumes();
        return ResponseEntity.ok(ApiResponse.success("All resumes retrieved successfully", resumes));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resume", description = "Delete a resume by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @Parameter(description = "Resume ID") @PathVariable Long id) {
        try {
            resumeParserService.deleteResume(id);
            return ResponseEntity.ok(ApiResponse.success("Resume deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting resume with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete resume"));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the resume parser service is healthy")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Resume Parser Service is healthy", "OK"));
    }
}
