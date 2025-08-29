package com.resumescanner.resumeparser.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current")
    private Boolean isCurrent = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "work_experience_responsibilities", joinColumns = @JoinColumn(name = "work_experience_id"))
    @Column(name = "responsibility")
    private List<String> responsibilities = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "work_experience_achievements", joinColumns = @JoinColumn(name = "work_experience_id"))
    @Column(name = "achievement")
    private List<String> achievements = new ArrayList<>();

    @Column(name = "location")
    private String location;
}
