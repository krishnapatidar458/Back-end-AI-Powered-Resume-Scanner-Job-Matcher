package com.resumescanner.shared.dto;

import com.resumescanner.shared.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchRequest {
    private String keyword;
    private String title;
    private String location;
    private String companyName;
    private JobType jobType;
    private Boolean remoteAllowed;
    private Integer minExperienceYears;
    private Integer maxExperienceYears;
    private Double minSalary;
    private Double maxSalary;
    private String salaryCurrency;
}
