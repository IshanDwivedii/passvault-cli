package com.ishan.passvault.util;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive input validation utility to prevent SQL injection, XSS, and other attacks
 */
public class InputValidator {
    
    // SQL Injection patterns
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|WHERE|OR|AND|--|;|'|\"|\\/\\*|\\*\\/|xp_|sp_)"
    );
    
    // XSS patterns
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|javascript:|vbscript:|onload=|onerror=|onclick=|<iframe|<object|<embed|<form|<input|<textarea|<select|<button|<link|<meta|<style)"
    );
    
    // HTML injection patterns
    private static final Pattern HTML_INJECTION_PATTERN = Pattern.compile(
        "(?i)(<[^>]*>|&[a-zA-Z]+;|&#[0-9]+;)"
    );
    
    // Path traversal patterns
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(\\.\\.\\/|\\.\\.\\\\)"
    );
    
    // Command injection patterns
    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
        "(?i)(\\||&|;|`|\\$|\\(|\\)|\\{|\\}|\\[|\\]|<|>|\\?)"
    );
    
    // Null byte patterns
    private static final Pattern NULL_BYTE_PATTERN = Pattern.compile(
        "\\x00"
    );
    
    // Length limits
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_SERVICE_NAME_LENGTH = 100;
    private static final int MAX_PASSWORD_LENGTH = 1000;
    private static final int MAX_NOTES_LENGTH = 500;
    private static final int MAX_CATEGORY_LENGTH = 50;
    private static final int MAX_SEARCH_TERM_LENGTH = 200;
    
    // Allowed characters for different fields
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    private static final Pattern SERVICE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s._-]{1,100}$");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s_-]{1,50}$");
    
    /**
     * Validates and sanitizes username input
     * @param username The username to validate
     * @return Validated username or throws exception
     * @throws IllegalArgumentException if validation fails
     */
    public static String validateUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        
        String trimmed = username.trim();
        
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (trimmed.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Username cannot exceed " + MAX_USERNAME_LENGTH + " characters");
        }
        
        // Check for SQL injection
        if (containsSQLInjection(trimmed)) {
            throw new IllegalArgumentException("Username contains invalid characters");
        }
        
        // Check for XSS
        if (containsXSS(trimmed)) {
            throw new IllegalArgumentException("Username contains invalid characters");
        }
        
        // Check for null bytes
        if (containsNullBytes(trimmed)) {
            throw new IllegalArgumentException("Username contains invalid characters");
        }
        
        // Validate format
        if (!USERNAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Username must contain only letters, numbers, underscores, and hyphens (3-50 characters)");
        }
        
        return trimmed;
    }
    
    /**
     * Validates and sanitizes service name input
     * @param serviceName The service name to validate
     * @return Validated service name or throws exception
     * @throws IllegalArgumentException if validation fails
     */
    public static String validateServiceName(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("Service name cannot be null");
        }
        
        String trimmed = serviceName.trim();
        
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be empty");
        }
        
        if (trimmed.length() > MAX_SERVICE_NAME_LENGTH) {
            throw new IllegalArgumentException("Service name cannot exceed " + MAX_SERVICE_NAME_LENGTH + " characters");
        }
        
        // Check for SQL injection
        if (containsSQLInjection(trimmed)) {
            throw new IllegalArgumentException("Service name contains invalid characters");
        }
        
        // Check for XSS
        if (containsXSS(trimmed)) {
            throw new IllegalArgumentException("Service name contains invalid characters");
        }
        
        // Check for null bytes
        if (containsNullBytes(trimmed)) {
            throw new IllegalArgumentException("Service name contains invalid characters");
        }
        
        // Validate format
        if (!SERVICE_NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Service name contains invalid characters");
        }
        
        return trimmed;
    }
    
    /**
     * Validates and sanitizes password input
     * @param password The password to validate
     * @return Validated password or throws exception
     * @throws IllegalArgumentException if validation fails
     */
    public static String validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
        
        // Check for null bytes
        if (containsNullBytes(password)) {
            throw new IllegalArgumentException("Password contains invalid characters");
        }
        
        // Check for command injection patterns
        if (containsCommandInjection(password)) {
            throw new IllegalArgumentException("Password contains invalid characters");
        }
        
        return password;
    }
    
    /**
     * Validates and sanitizes notes input
     * @param notes The notes to validate
     * @return Validated notes or throws exception
     * @throws IllegalArgumentException if validation fails
     */
    public static String validateNotes(String notes) {
        if (notes == null) {
            return null; // Notes are optional
        }
        
        String trimmed = notes.trim();
        
        if (trimmed.isEmpty()) {
            return null;
        }
        
        if (trimmed.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException("Notes cannot exceed " + MAX_NOTES_LENGTH + " characters");
        }
        
        // Check for SQL injection
        if (containsSQLInjection(trimmed)) {
            throw new IllegalArgumentException("Notes contain invalid characters");
        }
        
        // Check for XSS
        if (containsXSS(trimmed)) {
            throw new IllegalArgumentException("Notes contain invalid characters");
        }
        
        // Check for null bytes
        if (containsNullBytes(trimmed)) {
            throw new IllegalArgumentException("Notes contain invalid characters");
        }
        
        // Sanitize HTML
        return sanitizeHTML(trimmed);
    }
    
    /**
     * Validates and sanitizes category input
     * @param category The category to validate
     * @return Validated category or throws exception
     * @throws IllegalArgumentException if validation fails
     */
    public static String validateCategory(String category) {
        if (category == null) {
            return null; // Category is optional
        }
        
        String trimmed = category.trim();
        
        if (trimmed.isEmpty()) {
            return null;
        }
        
        if (trimmed.length() > MAX_CATEGORY_LENGTH) {
            throw new IllegalArgumentException("Category cannot exceed " + MAX_CATEGORY_LENGTH + " characters");
        }
        
        // Check for SQL injection
        if (containsSQLInjection(trimmed)) {
            throw new IllegalArgumentException("Category contains invalid characters");
        }
        
        // Check for XSS
        if (containsXSS(trimmed)) {
            throw new IllegalArgumentException("Category contains invalid characters");
        }
        
        // Check for null bytes
        if (containsNullBytes(trimmed)) {
            throw new IllegalArgumentException("Category contains invalid characters");
        }
        
        // Validate format
        if (!CATEGORY_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Category contains invalid characters");
        }
        
        return trimmed;
    }
    
    /**
     * Validates and sanitizes search term input
     * @param searchTerm The search term to validate
     * @return Validated search term or throws exception
     * @throws IllegalArgumentException if validation fails
     */
    public static String validateSearchTerm(String searchTerm) {
        if (searchTerm == null) {
            throw new IllegalArgumentException("Search term cannot be null");
        }
        
        String trimmed = searchTerm.trim();
        
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        
        if (trimmed.length() > MAX_SEARCH_TERM_LENGTH) {
            throw new IllegalArgumentException("Search term cannot exceed " + MAX_SEARCH_TERM_LENGTH + " characters");
        }
        
        // Check for SQL injection
        if (containsSQLInjection(trimmed)) {
            throw new IllegalArgumentException("Search term contains invalid characters");
        }
        
        // Check for XSS
        if (containsXSS(trimmed)) {
            throw new IllegalArgumentException("Search term contains invalid characters");
        }
        
        // Check for null bytes
        if (containsNullBytes(trimmed)) {
            throw new IllegalArgumentException("Search term contains invalid characters");
        }
        
        // Check for path traversal
        if (containsPathTraversal(trimmed)) {
            throw new IllegalArgumentException("Search term contains invalid characters");
        }
        
        return trimmed;
    }
    
    /**
     * Checks if input contains SQL injection patterns
     * @param input The input to check
     * @return true if SQL injection patterns found
     */
    private static boolean containsSQLInjection(String input) {
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }
    
    /**
     * Checks if input contains XSS patterns
     * @param input The input to check
     * @return true if XSS patterns found
     */
    private static boolean containsXSS(String input) {
        return XSS_PATTERN.matcher(input).find();
    }
    
    /**
     * Checks if input contains HTML injection patterns
     * @param input The input to check
     * @return true if HTML injection patterns found
     */
    private static boolean containsHTMLInjection(String input) {
        return HTML_INJECTION_PATTERN.matcher(input).find();
    }
    
    /**
     * Checks if input contains path traversal patterns
     * @param input The input to check
     * @return true if path traversal patterns found
     */
    private static boolean containsPathTraversal(String input) {
        return PATH_TRAVERSAL_PATTERN.matcher(input).find();
    }
    
    /**
     * Checks if input contains command injection patterns
     * @param input The input to check
     * @return true if command injection patterns found
     */
    private static boolean containsCommandInjection(String input) {
        return COMMAND_INJECTION_PATTERN.matcher(input).find();
    }
    
    /**
     * Checks if input contains null bytes
     * @param input The input to check
     * @return true if null bytes found
     */
    private static boolean containsNullBytes(String input) {
        return NULL_BYTE_PATTERN.matcher(input).find() || input.contains("\0");
    }
    
    /**
     * Sanitizes HTML content by escaping special characters
     * @param input The input to sanitize
     * @return Sanitized input
     */
    private static String sanitizeHTML(String input) {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }
    
    /**
     * Validates all password entry fields at once
     * @param serviceName The service name
     * @param username The username
     * @param password The password
     * @param notes The notes (optional)
     * @param category The category (optional)
     * @return Array of validated values [serviceName, username, password, notes, category]
     * @throws IllegalArgumentException if any validation fails
     */
    public static String[] validatePasswordEntry(String serviceName, String username, String password, String notes, String category) {
        return new String[]{
            validateServiceName(serviceName),
            validateUsername(username),
            validatePassword(password),
            validateNotes(notes),
            validateCategory(category)
        };
    }
    
    /**
     * Logs security violations for monitoring
     * @param field The field name
     * @param value The attempted value
     * @param violation The type of violation
     */
    public static void logSecurityViolation(String field, String value, String violation) {
        // In production, this should log to a secure logging system
        System.err.println("SECURITY VIOLATION - Field: " + field + ", Value: " + value + ", Violation: " + violation);
    }
} 