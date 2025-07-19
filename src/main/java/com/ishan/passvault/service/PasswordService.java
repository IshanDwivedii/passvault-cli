package com.ishan.passvault.service;

import com.ishan.passvault.model.PasswordEntry;
import com.ishan.passvault.model.User;
import com.ishan.passvault.repository.PasswordEntryRepository;
import com.ishan.passvault.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class PasswordService {

    @Autowired
    private PasswordEntryRepository passwordEntryRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    public PasswordEntry addPassword(User user, String serviceName, String username, String password, String notes, String category) throws Exception {
        log.info("Adding password entry for user: {}, service: {}", user.getUsername(), serviceName);
        
        // Validate input
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be null or empty");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Check if entry already exists
        Optional<PasswordEntry> existingEntry = passwordEntryRepository.findByUserAndServiceNameAndUsername(
                user, serviceName.trim(), username.trim());
        if (existingEntry.isPresent()) {
            log.warn("Password entry already exists for user: {}, service: {}, username: {}", 
                    user.getUsername(), serviceName, username);
            throw new RuntimeException("Password entry already exists for this service and username");
        }

        // Encrypt password
        String encryptedPassword = encryptionService.encrypt(password, user.getMasterPasswordHash());
        
        // Create password entry
        PasswordEntry entry = PasswordEntry.builder()
                .user(user)
                .serviceName(serviceName.trim())
                .username(username.trim())
                .encryptedPassword(encryptedPassword)
                .notes(notes != null ? notes.trim() : null)
                .category(category != null ? category.trim() : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PasswordEntry savedEntry = passwordEntryRepository.save(entry);
        log.info("Password entry added successfully for user: {}, service: {}", user.getUsername(), serviceName);
        return savedEntry;
    }

    public String getPassword(User user, String serviceName, String username) throws Exception {
        log.info("Retrieving password for user: {}, service: {}, username: {}", 
                user.getUsername(), serviceName, username);
        
        Optional<PasswordEntry> entryOpt = passwordEntryRepository.findByUserAndServiceNameAndUsername(
                user, serviceName, username);
        if (entryOpt.isPresent()) {
            PasswordEntry entry = entryOpt.get();
            
            // Update last accessed time
            entry.markAsAccessed();
            passwordEntryRepository.save(entry);
            
            // Decrypt and return password
            String decryptedPassword = encryptionService.decrypt(entry.getEncryptedPassword(), user.getMasterPasswordHash());
            log.info("Password retrieved successfully for user: {}, service: {}", user.getUsername(), serviceName);
            return decryptedPassword;
        }
        
        log.warn("Password entry not found for user: {}, service: {}, username: {}", 
                user.getUsername(), serviceName, username);
        return null;
    }

    public List<PasswordEntry> getAllPasswords(User user) {
        log.info("Retrieving all passwords for user: {}", user.getUsername());
        List<PasswordEntry> entries = passwordEntryRepository.findByUserOrderByServiceNameAsc(user);
        log.info("Retrieved {} password entries for user: {}", entries.size(), user.getUsername());
        return entries;
    }
    
    public List<PasswordEntry> getPasswordsByCategory(User user, String category) {
        log.info("Retrieving passwords by category for user: {}, category: {}", user.getUsername(), category);
        List<PasswordEntry> entries = passwordEntryRepository.findByUserAndCategoryOrderByServiceNameAsc(user, category);
        log.info("Retrieved {} password entries for user: {}, category: {}", entries.size(), user.getUsername(), category);
        return entries;
    }
    
    public List<PasswordEntry> searchPasswords(User user, String searchTerm) {
        log.info("Searching passwords for user: {}, search term: {}", user.getUsername(), searchTerm);
        List<PasswordEntry> entries = passwordEntryRepository.findByUserAndSearchTerm(user, searchTerm);
        log.info("Found {} password entries for user: {}, search term: {}", entries.size(), user.getUsername(), searchTerm);
        return entries;
    }
    
    public List<String> getCategories(User user) {
        log.info("Retrieving categories for user: {}", user.getUsername());
        List<String> categories = passwordEntryRepository.findDistinctCategoriesByUser(user);
        log.info("Retrieved {} categories for user: {}", categories.size(), user.getUsername());
        return categories;
    }

    public void deletePassword(User user, String serviceName) {
        log.info("Deleting password entry for user: {}, service: {}", user.getUsername(), serviceName);
        
        Optional<PasswordEntry> entryOpt = passwordEntryRepository.findByUserAndServiceName(user, serviceName);
        if (entryOpt.isPresent()) {
            passwordEntryRepository.deleteByUserAndServiceName(user, serviceName);
            log.info("Password entry deleted successfully for user: {}, service: {}", user.getUsername(), serviceName);
        } else {
            log.warn("Password entry not found for deletion: user: {}, service: {}", user.getUsername(), serviceName);
            throw new RuntimeException("Password entry not found for service: " + serviceName);
        }
    }
    
    public void deletePassword(User user, String serviceName, String username) {
        log.info("Deleting password entry for user: {}, service: {}, username: {}", 
                user.getUsername(), serviceName, username);
        
        Optional<PasswordEntry> entryOpt = passwordEntryRepository.findByUserAndServiceNameAndUsername(
                user, serviceName, username);
        if (entryOpt.isPresent()) {
            passwordEntryRepository.deleteByUserAndServiceNameAndUsername(user, serviceName, username);
            log.info("Password entry deleted successfully for user: {}, service: {}, username: {}", 
                    user.getUsername(), serviceName, username);
        } else {
            log.warn("Password entry not found for deletion: user: {}, service: {}, username: {}", 
                    user.getUsername(), serviceName, username);
            throw new RuntimeException("Password entry not found for service: " + serviceName + " and username: " + username);
        }
    }
    
    public PasswordEntry updatePassword(User user, String serviceName, String username, String newPassword, String notes, String category) throws Exception {
        log.info("Updating password entry for user: {}, service: {}, username: {}", 
                user.getUsername(), serviceName, username);
        
        Optional<PasswordEntry> entryOpt = passwordEntryRepository.findByUserAndServiceNameAndUsername(
                user, serviceName, username);
        if (entryOpt.isPresent()) {
            PasswordEntry entry = entryOpt.get();
            
            // Encrypt new password
            String encryptedPassword = encryptionService.encrypt(newPassword, user.getMasterPasswordHash());
            
            // Update entry
            entry.setEncryptedPassword(encryptedPassword);
            entry.setNotes(notes != null ? notes.trim() : null);
            entry.setCategory(category != null ? category.trim() : null);
            entry.setUpdatedAt(LocalDateTime.now());
            
            PasswordEntry updatedEntry = passwordEntryRepository.save(entry);
            log.info("Password entry updated successfully for user: {}, service: {}", user.getUsername(), serviceName);
            return updatedEntry;
        } else {
            log.warn("Password entry not found for update: user: {}, service: {}, username: {}", 
                    user.getUsername(), serviceName, username);
            throw new RuntimeException("Password entry not found for service: " + serviceName + " and username: " + username);
        }
    }
    
    public long getPasswordCount(User user) {
        return passwordEntryRepository.countByUser(user);
    }
    
    public long getPasswordCountByCategory(User user, String category) {
        return passwordEntryRepository.countByUserAndCategory(user, category);
    }
    
    public List<PasswordEntry> getOldEntries(User user, LocalDateTime cutoffDate) {
        log.info("Retrieving old password entries for user: {}, cutoff date: {}", user.getUsername(), cutoffDate);
        List<PasswordEntry> entries = passwordEntryRepository.findOldEntriesByUser(user, cutoffDate);
        log.info("Retrieved {} old password entries for user: {}", entries.size(), user.getUsername());
        return entries;
    }
}
