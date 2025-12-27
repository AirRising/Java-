package com.usermanagement.dao;

import com.usermanagement.model.User;
import java.util.List;

/**
 * 用户数据访问接口
 */
public interface UserDao {

    /**
     * 添加用户
     */
    boolean addUser(User user);

    /**
     * 根据ID删除用户
     */
    boolean deleteUser(int userId);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 根据用户ID查询用户
     */
    User getUserById(int userId);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 验证用户登录
     */
    User validateLogin(String username, String password, String userType);

    /**
     * 获取所有用户
     */
    List<User> getAllUsers();

    /**
     * 根据用户类型获取用户
     */
    List<User> getUsersByType(String userType);

    /**
     * 获取待审核用户
     */
    List<User> getPendingUsers();

    /**
     * 更新用户登录时间
     */
    boolean updateLoginTime(int userId);

    /**
     * 更新用户审核状态
     */
    boolean updateApprovalStatus(int userId, String status);
}