package com.resumescanner.jobmatcher.service;

import com.resumescanner.jobmatcher.entity.Job;
import com.resumescanner.jobmatcher.repository.JobRepository;
import com.resumescanner.shared.dto.JobDTO;
import com.resumescanner.shared.enums.JobType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public JobDTO createJob(JobDTO jobDTO) {
        Job job = new Job();
        mapDtoToEntity(jobDTO, job);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setIsActive(true);
        
        Job savedJob = jobRepository.save(job);
        log.info("Job created with ID: {}", savedJob.getId());
        
        return convertToDTO(savedJob);
    }

    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
        return convertToDTO(job);
    }

    public Page<JobDTO> getAllJobs(Pageable pageable) {
        return jobRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDTO);
    }

    public List<JobDTO> getActiveJobs() {
        return jobRepository.findByIsActive(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<JobDTO> searchJobs(String keyword, Pageable pageable) {
        return jobRepository.searchByKeyword(keyword, pageable)
                .map(this::convertToDTO);
    }

    public Page<JobDTO> searchJobsAdvanced(String title, String location, String companyName, 
                                          JobType jobType, Boolean remoteAllowed, Pageable pageable) {
        return jobRepository.searchJobs(title, location, companyName, jobType, remoteAllowed, pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public JobDTO updateJob(Long id, JobDTO jobDTO) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
        
        mapDtoToEntity(jobDTO, job);
        job.setUpdatedAt(LocalDateTime.now());
        
        Job updatedJob = jobRepository.save(job);
        log.info("Job updated with ID: {}", updatedJob.getId());
        
        return convertToDTO(updatedJob);
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
        
        job.setIsActive(false);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
        
        log.info("Job deactivated with ID: {}", id);
    }

    public List<JobDTO> getJobsByCompany(String companyName) {
        return jobRepository.findByCompanyName(companyName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobDTO> getJobsByType(JobType jobType) {
        return jobRepository.findByJobType(jobType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobDTO> getJobsByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Long getActiveJobsCount() {
        return jobRepository.countActiveJobs();
    }

    private void mapDtoToEntity(JobDTO dto, Job entity) {
        entity.setTitle(dto.getTitle());
        entity.setCompanyName(dto.getCompanyName());
        entity.setDescription(dto.getDescription());
        entity.setRequirements(dto.getRequirements());
        entity.setRequiredSkills(dto.getRequiredSkills());
        entity.setExperienceLevel(dto.getExperienceLevel());
        entity.setMinExperienceYears(dto.getMinExperienceYears());
        entity.setMaxExperienceYears(dto.getMaxExperienceYears());
        entity.setMinSalary(dto.getMinSalary());
        entity.setMaxSalary(dto.getMaxSalary());
        entity.setSalaryCurrency(dto.getSalaryCurrency());
        entity.setLocation(dto.getLocation());
        entity.setRemoteAllowed(dto.getRemoteAllowed());
        entity.setJobType(dto.getJobType());
        entity.setBenefits(dto.getBenefits());
        entity.setQualifications(dto.getQualifications());
        entity.setApplicationDeadline(dto.getApplicationDeadline());
        entity.setPostedBy(dto.getPostedBy());
    }

    private JobDTO convertToDTO(Job job) {
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompanyName(job.getCompanyName());
        dto.setDescription(job.getDescription());
        dto.setRequirements(job.getRequirements());
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setMinExperienceYears(job.getMinExperienceYears());
        dto.setMaxExperienceYears(job.getMaxExperienceYears());
        dto.setMinSalary(job.getMinSalary());
        dto.setMaxSalary(job.getMaxSalary());
        dto.setSalaryCurrency(job.getSalaryCurrency());
        dto.setLocation(job.getLocation());
        dto.setRemoteAllowed(job.getRemoteAllowed());
        dto.setJobType(job.getJobType());
        dto.setBenefits(job.getBenefits());
        dto.setQualifications(job.getQualifications());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        dto.setIsActive(job.getIsActive());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setUpdatedAt(job.getUpdatedAt());
        dto.setPostedBy(job.getPostedBy());
        return dto;
    }
}
