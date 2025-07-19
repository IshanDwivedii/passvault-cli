package com.ishan.passvault.repository;

import com.ishan.passvault.model.PasswordEntry;
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
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {
    
    List<PasswordEntry> findByUserOrderByServiceNameAsc(User user);
    
    List<PasswordEntry> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<PasswordEntry> findByUserAndServiceName(User user, String serviceName);
    
    Optional<PasswordEntry> findByUserAndServiceNameAndUsername(User user, String serviceName, String username);
    
    List<PasswordEntry> findByUserAndServiceNameContainingIgnoreCase(User user, String serviceName);
    
    List<PasswordEntry> findByUserAndCategory(User user, String category);
    
    List<PasswordEntry> findByUserAndCategoryOrderByServiceNameAsc(User user, String category);
    
    void deleteByUserAndServiceName(User user, String serviceName);
    
    void deleteByUserAndServiceNameAndUsername(User user, String serviceName, String username);
    
    long countByUser(User user);
    
    long countByUserAndCategory(User user, String category);
    
    @Query("SELECT pe FROM PasswordEntry pe WHERE pe.user = :user AND (pe.serviceName LIKE %:searchTerm% OR pe.username LIKE %:searchTerm% OR pe.notes LIKE %:searchTerm%)")
    List<PasswordEntry> findByUserAndSearchTerm(@Param("user") User user, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT DISTINCT pe.category FROM PasswordEntry pe WHERE pe.user = :user AND pe.category IS NOT NULL ORDER BY pe.category")
    List<String> findDistinctCategoriesByUser(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE PasswordEntry pe SET pe.lastAccessed = :lastAccessed WHERE pe.id = :entryId")
    void updateLastAccessed(@Param("entryId") Long entryId, @Param("lastAccessed") LocalDateTime lastAccessed);
    
    @Query("SELECT pe FROM PasswordEntry pe WHERE pe.user = :user AND pe.lastAccessed < :date ORDER BY pe.lastAccessed ASC")
    List<PasswordEntry> findOldEntriesByUser(@Param("user") User user, @Param("date") LocalDateTime date);
}
