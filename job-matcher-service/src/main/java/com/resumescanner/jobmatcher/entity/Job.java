package com.resumescanner.jobmatcher.entity;

import com.resumescanner.shared.enums.JobType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> requiredSkills = new ArrayList<>();
    
    @Column(name = "experience_level")
    private String experienceLevel; // Entry, Mid, Senior, Executive
    
    @Column(name = "min_experience_years")
    private Integer minExperienceYears;
    
    @Column(name = "max_experience_years")
    private Integer maxExperienceYears;
    
    @Column(name = "min_salary")
    private Double minSalary;
    
    @Column(name = "max_salary")
    private Double maxSalary;
    
    @Column(name = "salary_currency")
    private String salaryCurrency = "USD";
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "remote_allowed")
    private Boolean remoteAllowed = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType = JobType.FULL_TIME;
    
    @ElementCollection
    @CollectionTable(name = "job_benefits", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "job_qualifications", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "qualification")
    private List<String> qualifications = new ArrayList<>();
    
    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "posted_by")
    private Long postedBy; // User ID who posted the job
}
