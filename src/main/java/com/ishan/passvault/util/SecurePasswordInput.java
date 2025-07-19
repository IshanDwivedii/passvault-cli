package com.ishan.passvault.util;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Utility class for secure password input that masks passwords and uses char arrays
 * for better memory security
 */
public class SecurePasswordInput {
    
    private static final String MASK_CHAR = "*";
    private static final int MAX_PASSWORD_LENGTH = 1000;
    
    /**
     * Reads a password securely with masking
     * @param prompt The prompt to display
     * @return char array containing the password (caller must clear this)
     * @throws IOException if input fails
     */
    public static char[] readPassword(String prompt) throws IOException {
        System.out.print(prompt);
        
        // Try to use Console for better security (masks input automatically)
        Console console = System.console();
        if (console != null) {
            char[] password = console.readPassword();
            if (password == null) {
                throw new IOException("Password input was interrupted");
            }
            return password;
        }
        
        // Fallback to Scanner with manual masking
        return readPasswordWithMasking(prompt);
    }
    
    /**
     * Reads a password with manual masking using Scanner
     * @param prompt The prompt to display
     * @return char array containing the password
     * @throws IOException if input fails
     */
    private static char[] readPasswordWithMasking(String prompt) throws IOException {
        Scanner scanner = new Scanner(System.in);
        StringBuilder password = new StringBuilder();
        
        try {
            while (true) {
                int input = System.in.read();
                
                if (input == -1) {
                    throw new IOException("End of input reached");
                }
                
                char c = (char) input;
                
                // Handle backspace
                if (c == '\b' || c == 127) {
                    if (password.length() > 0) {
                        password.setLength(password.length() - 1);
                        System.out.print("\b \b"); // Clear the character
                    }
                    continue;
                }
                
                // Handle enter key
                if (c == '\n' || c == '\r') {
                    System.out.println(); // New line
                    break;
                }
                
                // Handle other control characters
                if (c < 32) {
                    continue;
                }
                
                // Add character if within limit
                if (password.length() < MAX_PASSWORD_LENGTH) {
                    password.append(c);
                    System.out.print(MASK_CHAR);
                }
            }
        } finally {
            // Don't close scanner as it's shared
        }
        
        return password.toString().toCharArray();
    }
    
    /**
     * Reads a password with confirmation
     * @param prompt The prompt to display
     * @param confirmPrompt The confirmation prompt
     * @return char array containing the password
     * @throws IOException if input fails
     */
    public static char[] readPasswordWithConfirmation(String prompt, String confirmPrompt) throws IOException {
        char[] password = readPassword(prompt);
        
        try {
            char[] confirmPassword = readPassword(confirmPrompt);
            
            try {
                if (!Arrays.equals(password, confirmPassword)) {
                    System.out.println("Passwords do not match!");
                    return null;
                }
                return password;
            } finally {
                // Clear confirmation password from memory
                Arrays.fill(confirmPassword, '\0');
            }
        } catch (Exception e) {
            // Clear password from memory on error
            Arrays.fill(password, '\0');
            throw e;
        }
    }
    
    /**
     * Validates password strength
     * @param password The password to validate
     * @return true if password meets requirements
     */
    public static boolean isValidPassword(char[] password) {
        if (password == null || password.length < 8) {
            return false;
        }
        
        if (password.length > MAX_PASSWORD_LENGTH) {
            return false;
        }
        
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        
        for (char c : password) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Gets password strength description
     * @param password The password to analyze
     * @return Strength description
     */
    public static String getPasswordStrength(char[] password) {
        if (password == null || password.length < 8) {
            return "Very Weak";
        }
        
        int score = 0;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        
        for (char c : password) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        
        if (password.length >= 12) score++;
        if (password.length >= 16) score++;
        
        switch (score) {
            case 0:
            case 1: return "Very Weak";
            case 2: return "Weak";
            case 3: return "Fair";
            case 4: return "Good";
            case 5: return "Strong";
            case 6: return "Very Strong";
            default: return "Excellent";
        }
    }
    
    /**
     * Safely clears a char array from memory
     * @param array The array to clear
     */
    public static void clearArray(char[] array) {
        if (array != null) {
            Arrays.fill(array, '\0');
        }
    }
    
    /**
     * Safely clears a byte array from memory
     * @param array The array to clear
     */
    public static void clearArray(byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte) 0);
        }
    }
} 