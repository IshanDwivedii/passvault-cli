package com.ishan.passvault.util;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private final SecureRandom random = new SecureRandom();

    public String generatePassword(int length, boolean includeSymbols) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder charset = new StringBuilder();
        charset.append(UPPERCASE).append(LOWERCASE).append(NUMBERS);

        if (includeSymbols) {
            charset.append(SYMBOLS);
        }

        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));

        if (includeSymbols) {
            password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }

        // Fill the rest randomly
        for (int i = password.length(); i < length; i++) {
            password.append(charset.charAt(random.nextInt(charset.length())));
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }

    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
