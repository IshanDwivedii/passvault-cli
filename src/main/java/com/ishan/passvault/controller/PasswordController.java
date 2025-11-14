package com.ishan.passvault.controller;

import com.ishan.passvault.model.PasswordEntry;
import com.ishan.passvault.model.User;
import com.ishan.passvault.repository.UserRepository;
import com.ishan.passvault.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/passwords")
@CrossOrigin(origins = "*")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllPasswords(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<PasswordEntry> passwords = passwordService.getAllPasswords(user);
            return ResponseEntity.ok(passwords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> addPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> data) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PasswordEntry entry = passwordService.addPassword(
                    user,
                    data.get("serviceName"),
                    data.get("username"),
                    data.get("password"),
                    data.get("notes"),
                    data.get("category"),
                    data.get("masterPassword")
            );

            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/decrypt")
    public ResponseEntity<?> getDecryptedPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> data) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String password = passwordService.getPassword(
                    user,
                    data.get("serviceName"),
                    data.get("username"),
                    data.get("masterPassword")
            );

            return ResponseEntity.ok(password);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deletePassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> data) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            passwordService.deletePassword(
                    user,
                    data.get("serviceName"),
                    data.get("username")
            );

            return ResponseEntity.ok(Map.of("message", "Password deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/search")
    public ResponseEntity<?> searchPasswords(
            @PathVariable Long userId,
            @RequestParam String q) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<PasswordEntry> passwords = passwordService.searchPasswords(user, q);
            return ResponseEntity.ok(passwords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
