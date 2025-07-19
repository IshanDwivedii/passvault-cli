package com.ishan.passvault.repository;

import com.ishan.passvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByUsernameAndIsActiveTrue(String username);
    
    boolean existsByUsername(String username);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date AND u.isActive = true")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);
    
    @Modifying
    @Query("UPDATE User u SET u.isActive = :isActive WHERE u.id = :userId")
    void updateUserStatus(@Param("userId") Long userId, @Param("isActive") Boolean isActive);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
}
