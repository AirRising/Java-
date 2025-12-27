package com.usermanagement.service;

import com.usermanagement.dao.UserDao;
import com.usermanagement.dao.UserDaoImpl;
import com.usermanagement.model.User;
import com.usermanagement.util.PasswordUtil;

/**
 * 认证服务的具体实现类
 */
public class AuthServiceImpl implements AuthService {

    private UserDao userDao;
    private User currentUser;

    public AuthServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public User login(String username, String password, String userType) {
        // 1. 参数校验
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        // 2. 调用DAO层验证用户
        User user = userDao.validateLogin(username, password, userType);

        if (user != null) {
            // 3. 登录成功，设置当前用户
            this.currentUser = user;
            System.out.println("用户 " + username + " 登录成功");
        } else {
            System.out.println("用户名或密码错误");
        }

        return user;
    }

    @Override
    public boolean register(User user, String confirmPassword) {
        // 1. 验证两次密码是否一致
        if (!user.getPassword().equals(confirmPassword)) {
            System.out.println("两次输入的密码不一致");
            return false;
        }

        // 2. 检查用户名是否已存在
        if (isUsernameExists(user.getUsername())) {
            System.out.println("用户名已存在");
            return false;
        }

        // 3. 验证密码强度
        if (!validatePasswordStrength(user.getPassword())) {
            System.out.println("密码强度不足，必须包含字母和数字，长度至少6位");
            return false;
        }

        // 4. 检查是否尝试注册管理员
        if (User.TYPE_ADMIN.equals(user.getUserType())) {
            System.out.println("管理员账号无需注册，请联系系统管理员");
            return false;
        }

        // 5. 设置默认状态为"待审核"
        user.setApprovalStatus(User.STATUS_PENDING);

        // 6. 保存用户到数据库
        boolean success = userDao.addUser(user);

        if (success) {
            System.out.println("注册成功！请等待管理员审核");
        } else {
            System.out.println("注册失败，请稍后重试");
        }

        return success;
    }

    @Override
    public boolean isUsernameExists(String username) {
        User user = userDao.getUserByUsername(username);
        return user != null;
    }

    @Override
    public boolean validatePasswordStrength(String password) {
        return PasswordUtil.checkPasswordStrength(password);
    }

    @Override
    public void logout() {
        System.out.println("用户 " + currentUser.getUsername() + " 已退出登录");
        this.currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return this.currentUser;
    }

    // 其他辅助方法
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            System.out.println("请先登录");
            return false;
        }

        // 验证旧密码
        if (!PasswordUtil.verify(oldPassword, currentUser.getPassword())) {
            System.out.println("原密码错误");
            return false;
        }

        // 验证新密码强度
        if (!validatePasswordStrength(newPassword)) {
            System.out.println("新密码强度不足");
            return false;
        }

        // 更新密码
        currentUser.setPassword(PasswordUtil.encrypt(newPassword));
        boolean success = userDao.updateUser(currentUser);

        if (success) {
            System.out.println("密码修改成功");
        } else {
            System.out.println("密码修改失败");
        }

        return success;
    }
}
