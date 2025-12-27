package com.usermanagement.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 密码工具类（简化版，实际项目应使用更安全的加密方式）
 */
public class PasswordUtil {

    /**
     * 密码加密（这里使用SHA-256，实际项目中建议使用BCrypt）
     */
    public static String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // 如果加密失败，返回原密码（仅用于演示，实际项目必须加密）
            return password;
        }
    }

    /**
     * 验证密码
     */
    public static boolean verify(String inputPassword, String storedPassword) {
        // 在实际项目中，应该对比加密后的密码
        String encryptedInput = encrypt(inputPassword);
        return encryptedInput.equals(storedPassword);
    }

    /**
     * 检查密码强度
     */
    public static boolean checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // 检查是否包含数字
        boolean hasDigit = password.matches(".*\\d.*");
        // 检查是否包含字母
        boolean hasLetter = password.matches(".*[a-zA-Z].*");

        return hasDigit && hasLetter;
    }
}