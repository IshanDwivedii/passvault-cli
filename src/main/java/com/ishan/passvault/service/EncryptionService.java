package com.ishan.passvault.service;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int KEY_LENGTH = 256; // bits
    private static final int ITERATION_COUNT = 100000; // OWASP recommended minimum
    private static final int SALT_LENGTH = 32; // bytes

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Encrypts plaintext using AES-GCM with proper key derivation
     * @param plainText The text to encrypt
     * @param masterPassword The master password for key derivation
     * @return Base64 encoded string containing salt + IV + encrypted data
     * @throws Exception if encryption fails
     */
    public String encrypt(String plainText, String masterPassword) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (masterPassword == null || masterPassword.isEmpty()) {
            throw new IllegalArgumentException("Master password cannot be null or empty");
        }

        try {
            // Generate salt and derive key
            byte[] salt = generateSalt();
            SecretKey key = deriveKeyFromPassword(masterPassword, salt);

            // Generate IV
            byte[] iv = generateIV();

            // Encrypt
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combine salt + IV + encrypted data
            byte[] combined = new byte[salt.length + iv.length + encryptedData.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(iv, 0, combined, salt.length, iv.length);
            System.arraycopy(encryptedData, 0, combined, salt.length + iv.length, encryptedData.length);

            // Clear sensitive data from memory
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(iv, (byte) 0);
            Arrays.fill(encryptedData, (byte) 0);

            return Base64.getEncoder().encodeToString(combined);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encryption algorithm not available", e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("Encryption padding not available", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid encryption key", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Invalid encryption parameters", e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Invalid block size for encryption", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("Bad padding during encryption", e);
        }
    }

    /**
     * Decrypts ciphertext using AES-GCM with proper key derivation
     * @param encryptedText Base64 encoded string containing salt + IV + encrypted data
     * @param masterPassword The master password for key derivation
     * @return Decrypted plaintext
     * @throws Exception if decryption fails
     */
    public String decrypt(String encryptedText, String masterPassword) throws Exception {
        if (encryptedText == null || encryptedText.isEmpty()) {
            throw new IllegalArgumentException("Encrypted text cannot be null or empty");
        }
        if (masterPassword == null || masterPassword.isEmpty()) {
            throw new IllegalArgumentException("Master password cannot be null or empty");
        }

        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            
            if (combined.length < SALT_LENGTH + GCM_IV_LENGTH + GCM_TAG_LENGTH) {
                throw new IllegalArgumentException("Encrypted data too short");
            }

            // Extract salt, IV, and encrypted data
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[combined.length - SALT_LENGTH - GCM_IV_LENGTH];
            
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combined, SALT_LENGTH, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, SALT_LENGTH + GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

            // Derive key and decrypt
            SecretKey key = deriveKeyFromPassword(masterPassword, salt);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);
            String result = new String(decryptedData, StandardCharsets.UTF_8);

            // Clear sensitive data from memory
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(iv, (byte) 0);
            Arrays.fill(encryptedData, (byte) 0);
            Arrays.fill(decryptedData, (byte) 0);

            return result;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid encrypted data format", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Decryption algorithm not available", e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("Decryption padding not available", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid decryption key", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Invalid decryption parameters", e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Invalid block size for decryption", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("Bad padding during decryption - possible data corruption or wrong password", e);
        }
    }

    /**
     * Derives a cryptographic key from password using PBKDF2
     * @param password The password to derive key from
     * @param salt The salt to use for key derivation
     * @return SecretKey for AES encryption
     * @throws Exception if key derivation fails
     */
    private SecretKey deriveKeyFromPassword(String password, byte[] salt) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (salt == null || salt.length != SALT_LENGTH) {
            throw new IllegalArgumentException("Salt must be exactly " + SALT_LENGTH + " bytes");
        }

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
            
            // Clear temporary key
            Arrays.fill(tmp.getEncoded(), (byte) 0);
            
            return secret;
        } catch (Exception e) {
            throw new RuntimeException("Key derivation failed", e);
        }
    }

    /**
     * Generates a cryptographically secure random salt
     * @return 32-byte salt
     */
    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Generates a cryptographically secure random IV
     * @return 12-byte IV for GCM
     */
    private byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }

    /**
     * Validates if a string can be encrypted/decrypted
     * @param text The text to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidText(String text) {
        return text != null && !text.isEmpty() && text.length() <= 10000; // Reasonable limit
    }

    /**
     * Validates if a password meets security requirements
     * @param password The password to validate
     * @return true if meets requirements, false otherwise
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.length() <= 1000;
    }
}
