package com.resumescanner.resumeparser.repository;

import com.resumescanner.resumeparser.entity.Resume;
import com.resumescanner.shared.enums.ResumeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    
    List<Resume> findByUserId(Long userId);
    
    List<Resume> findByStatus(ResumeStatus status);
    
    List<Resume> findByUserIdAndStatus(Long userId, ResumeStatus status);
    
    @Query("SELECT r FROM Resume r WHERE r.userId = :userId ORDER BY r.createdAt DESC")
    List<Resume> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Resume r WHERE r.fullName LIKE %:name%")
    List<Resume> findByFullNameContaining(@Param("name") String name);
    
    @Query("SELECT r FROM Resume r WHERE r.email = :email")
    List<Resume> findByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(r) FROM Resume r WHERE r.status = :status")
    Long countByStatus(@Param("status") ResumeStatus status);
    
    @Query("SELECT COUNT(r) FROM Resume r WHERE r.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
