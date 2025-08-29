package com.resumescanner.usermanagement.repository;

import com.resumescanner.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER'")
    Long countUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN'")
    Long countAdmins();
}
