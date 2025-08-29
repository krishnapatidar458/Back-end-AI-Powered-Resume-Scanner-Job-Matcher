package com.resumescanner.shared.dto;

import com.resumescanner.shared.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private Long id;
    
    @NotBlank(message = "Job title is required")
    private String title;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    private String description;
    private String requirements;
    private List<String> requiredSkills;
    private String experienceLevel;
    private Integer minExperienceYears;
    private Integer maxExperienceYears;
    private Double minSalary;
    private Double maxSalary;
    private String salaryCurrency;
    private String location;
    private Boolean remoteAllowed;
    
    @NotNull(message = "Job type is required")
    private JobType jobType;
    
    private List<String> benefits;
    private List<String> qualifications;
    private LocalDateTime applicationDeadline;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long postedBy;
}
