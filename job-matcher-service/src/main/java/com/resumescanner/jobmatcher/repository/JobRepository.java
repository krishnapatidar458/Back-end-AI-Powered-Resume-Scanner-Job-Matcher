package com.resumescanner.jobmatcher.repository;

import com.resumescanner.jobmatcher.entity.Job;
import com.resumescanner.shared.enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findByIsActive(Boolean isActive);
    
    Page<Job> findByIsActiveTrue(Pageable pageable);
    
    List<Job> findByCompanyName(String companyName);
    
    List<Job> findByJobType(JobType jobType);
    
    List<Job> findByLocationContainingIgnoreCase(String location);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Job> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:companyName IS NULL OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:remoteAllowed IS NULL OR j.remoteAllowed = :remoteAllowed)")
    Page<Job> searchJobs(@Param("title") String title,
                        @Param("location") String location,
                        @Param("companyName") String companyName,
                        @Param("jobType") JobType jobType,
                        @Param("remoteAllowed") Boolean remoteAllowed,
                        Pageable pageable);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.isActive = true")
    Long countActiveJobs();
    
    @Query("SELECT j FROM Job j WHERE j.postedBy = :userId")
    List<Job> findByPostedBy(@Param("userId") Long userId);
}
