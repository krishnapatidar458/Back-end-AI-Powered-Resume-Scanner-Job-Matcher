package com.resumescanner.jobmatcher.repository;

import com.resumescanner.jobmatcher.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    
    List<MatchResult> findByUserId(Long userId);
    
    List<MatchResult> findByResumeId(Long resumeId);
    
    List<MatchResult> findByJobId(Long jobId);
    
    List<MatchResult> findByUserIdAndResumeId(Long userId, Long resumeId);
    
    @Query("SELECT mr FROM MatchResult mr WHERE mr.userId = :userId ORDER BY mr.overallScore DESC")
    List<MatchResult> findByUserIdOrderByScoreDesc(@Param("userId") Long userId);
    
    @Query("SELECT mr FROM MatchResult mr WHERE mr.userId = :userId ORDER BY mr.createdAt DESC")
    List<MatchResult> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT mr FROM MatchResult mr WHERE mr.overallScore >= :minScore")
    List<MatchResult> findByMinimumScore(@Param("minScore") Double minScore);
    
    @Query("SELECT AVG(mr.overallScore) FROM MatchResult mr WHERE mr.userId = :userId")
    Double getAverageScoreForUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(mr) FROM MatchResult mr WHERE mr.userId = :userId")
    Long countMatchesByUser(@Param("userId") Long userId);
    
    @Query("SELECT mr FROM MatchResult mr WHERE mr.createdAt >= :startDate")
    List<MatchResult> findRecentMatches(@Param("startDate") LocalDateTime startDate);
}
