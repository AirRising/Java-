package com.usermanagement.service;

import com.usermanagement.model.User;

/**
 * 认证服务接口 - 定义了所有认证相关的方法签名
 */
public interface AuthService {

    // 方法声明（只有签名，没有实现）
    User login(String username, String password, String userType);

    boolean register(User user, String confirmPassword);

    boolean isUsernameExists(String username);

    boolean validatePasswordStrength(String password);

    void logout();

    User getCurrentUser();
}