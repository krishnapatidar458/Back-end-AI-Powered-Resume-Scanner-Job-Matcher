package com.resumescanner.shared.dto;

import com.resumescanner.shared.enums.ResumeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {
    private Long id;
    private Long userId;
    private String originalFilename;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private ResumeStatus status;
    
    // Parsed Content
    private String fullName;
    private String email;
    private String phone;
    private String summary;
    private List<String> skills;
    private List<ExperienceDTO> workExperiences;
    private List<EducationDTO> educations;
    private List<String> certifications;
    private List<String> languages;
    private String rawText;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime parsedAt;
}
