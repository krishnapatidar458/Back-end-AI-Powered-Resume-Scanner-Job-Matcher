package com.resumescanner.resumeparser.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "educations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;
    
    @Column(name = "institution_name", nullable = false)
    private String institutionName;
    
    @Column(name = "degree")
    private String degree;
    
    @Column(name = "field_of_study")
    private String fieldOfStudy;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "gpa")
    private Double gpa;
    
    @Column(name = "location")
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_current")
    private Boolean isCurrent = false;
}
