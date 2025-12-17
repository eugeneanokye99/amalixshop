package com.amalixshop.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for encrypting/decrypting sensitive data like customer IDs
 * Uses AES encryption for security
 */
public class EncryptionUtil {

    // 16, 24, or 32 bytes key for AES-128, AES-192, or AES-256
    private static final String SECRET_KEY = "MySecretKey123"; // 16 chars = 128 bits
    private static final String ALGORITHM = "AES";

    /**
     * Encrypts an ID for sending to frontend
     * @param Id The  ID to encrypt
     * @return Encrypted string (URL-safe)
     */
    public static String encrypt(int Id) {
        try {
            String plainText = String.valueOf(Id);
            SecretKeySpec secretKey = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Use URL-safe Base64 for web compatibility
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error encrypting customer ID", e);
        }
    }

    /**
     * Decrypts an encrypted customer ID from frontend
     * @param encryptedId The encrypted string
     * @return Original customer ID
     */
    public static int decrypt(String encryptedId) {
        try {
            SecretKeySpec secretKey = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedId);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            return Integer.parseInt(decryptedText);

        } catch (Exception e) {
            throw new RuntimeException("Error decrypting customer ID", e);
        }
    }

    /**
     * Generates a secret key from the SECRET_KEY string
     */
    private static SecretKeySpec generateKey() {
        // Ensure key is exactly 16, 24, or 32 bytes for AES
        byte[] key = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        byte[] paddedKey = new byte[16]; // AES-128 requires 16 bytes

        // Copy key bytes, pad with zeros if shorter
        System.arraycopy(key, 0, paddedKey, 0, Math.min(key.length, 16));

        return new SecretKeySpec(paddedKey, ALGORITHM);
    }

}