package com.ishan.passvault.service;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    public String encrypt(String plainText, String masterPassword) throws Exception {
        SecretKey key = deriveKeyFromPassword(masterPassword);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted data
        byte[] encryptedWithIv = new byte[encryptedData.length + iv.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    public String decrypt(String encryptedText, String masterPassword) throws Exception {
        byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
        
        // Extract IV and encrypted data
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
        System.arraycopy(encryptedWithIv, iv.length, encryptedData, 0, encryptedData.length);

        SecretKey key = deriveKeyFromPassword(masterPassword);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    private SecretKey deriveKeyFromPassword(String password) {
        byte[] key = password.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = new byte[32];
        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, 32));

        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
