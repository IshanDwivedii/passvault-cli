package com.ishan.passvault.service;

import com.ishan.passvault.model.User;
import com.ishan.passvault.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(String username, String masterPassword) {
        log.info("Attempting to register user: {}", username);
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (masterPassword == null || masterPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Master password cannot be null or empty");
        }
        
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            log.warn("User registration failed: username '{}' already exists", username);
            throw new RuntimeException("User with username '" + username + "' already exists");
        }

        // Create new user with BCrypt hashing
        User user = User.builder()
                .username(username.trim())
                .masterPasswordHash(passwordEncoder.encode(masterPassword))
                .salt("") // BCrypt handles salt internally
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", username);
        return savedUser;
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameAndIsActiveTrue(username);
    }
    
    public boolean authenticateUser(String username, String password) {
        log.info("Attempting to authenticate user: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsernameAndIsActiveTrue(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean isValid = passwordEncoder.matches(password, user.getMasterPasswordHash());
            
            if (isValid) {
                // Update last login
                userRepository.updateLastLogin(user.getId(), LocalDateTime.now());
                log.info("User authenticated successfully: {}", username);
            } else {
                log.warn("Authentication failed for user: {}", username);
            }
            
            return isValid;
        }
        
        log.warn("Authentication failed: user not found: {}", username);
        return false;
    }
    
    public User updateLastLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsernameAndIsActiveTrue(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userRepository.updateLastLogin(user.getId(), LocalDateTime.now());
            return user;
        }
        throw new RuntimeException("User not found: " + username);
    }
    
    public void deactivateUser(String username) {
        Optional<User> userOpt = userRepository.findByUsernameAndIsActiveTrue(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userRepository.updateUserStatus(user.getId(), false);
            log.info("User deactivated: {}", username);
        } else {
            throw new RuntimeException("User not found: " + username);
        }
    }
    
    public void activateUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userRepository.updateUserStatus(user.getId(), true);
            log.info("User activated: {}", username);
        } else {
            throw new RuntimeException("User not found: " + username);
        }
    }
    
    public List<User> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }
    
    public List<User> getInactiveUsers(LocalDateTime cutoffDate) {
        return userRepository.findInactiveUsers(cutoffDate);
    }
    
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        log.info("Attempting to change password for user: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsernameAndIsActiveTrue(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Verify old password
            if (!passwordEncoder.matches(oldPassword, user.getMasterPasswordHash())) {
                log.warn("Password change failed: incorrect old password for user: {}", username);
                return false;
            }
            
            // Update password
            user.setMasterPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            log.info("Password changed successfully for user: {}", username);
            return true;
        }
        
        log.warn("Password change failed: user not found: {}", username);
        return false;
    }
}
