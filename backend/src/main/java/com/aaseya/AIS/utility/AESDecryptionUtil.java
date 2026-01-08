package com.aaseya.AIS.utility;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class AESDecryptionUtil {

    // Define the AES key and IV (Initialization Vector)
    private static final String secretKey = "0123456789ABCDEF";
    private static final String iv = "1234567890ABCDEF";

    // Method to check if the input string is a valid Base64-encoded string
    private static boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);  // Try decoding
            return true;  // If successful, it's Base64-encoded
        } catch (IllegalArgumentException e) {
            return false;  // If exception, it's not Base64
        }
    }

    public static String decrypt(String encryptedPassword) {
        try {
            // If the input is not Base64-encoded, return it as plain text
            if (!isBase64(encryptedPassword)) {
                System.out.println("Plain text input: " + encryptedPassword);
                return encryptedPassword;  // Return plain text as-is
            }

            // Decode the encrypted password from Base64
            byte[] decodedPassword = Base64.getDecoder().decode(encryptedPassword);

            // Set up AES decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

            // Initialize cipher
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // Perform decryption
            byte[] decrypted = cipher.doFinal(decodedPassword);

            // Convert decrypted bytes to plain-text
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace(); // Log error for debugging
            throw new RuntimeException("Error during AES decryption", e);
        }
    }

	/*
	 * public static void main(String[] args) { // Test with both encrypted and
	 * plain text inputs
	 * 
	 * // Encrypted Base64 string (example) String encryptedBase64 =
	 * "T5vGyIoZCsrnZ8Nt/hM2IQ=="; System.out.println("Decrypted: " +
	 * decrypt(encryptedBase64)); // Decrypts correctly
	 * 
	 * // Plain text (not encrypted) String plainText = "rules";
	 * System.out.println("Decrypted: " + decrypt(plainText)); // Returns plain text
	 * as-is }
	 */
}
