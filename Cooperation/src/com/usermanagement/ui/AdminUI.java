package com.usermanagement.ui;

import com.usermanagement.dao.UserDao;
import com.usermanagement.dao.UserDaoImpl;
import com.usermanagement.model.User;
import com.usermanagement.util.ConsoleUtil;

import java.util.List;
import java.util.Scanner;

/**
 * 管理员界面类
 */
public class AdminUI {
    private Scanner scanner;
    private User currentUser;  // 当前登录的管理员
    private UserDao userDao;

    public AdminUI(User adminUser) {
        this.scanner = new Scanner(System.in);
        this.currentUser = adminUser;
        this.userDao = new UserDaoImpl();
    }

    /**
     * 显示管理员主菜单
     */
    public void show() {
        while (true) {
            try {
                displayAdminMenu();
                int choice = getUserChoice();

                switch (choice) {
                    case 1: // 审批用户
                        handleUserApproval();
                        break;
                    case 2: // 添加用户
                        handleAddUser();
                        break;
                    case 3: // 删除用户
                        handleDeleteUser();
                        break;
                    case 4: // 查看用户列表
                        handleViewUsers();
                        break;
                    case 5: // 查看待审核用户
                        handleViewPendingUsers();
                        break;
                    case 6: // 修改密码
                        handleChangePassword();
                        break;
                    case 0: // 退出登录
                        System.out.println("已退出管理员系统");
                        return;
                    default:
                        System.out.println("无效的选择，请重新输入！");
                }

            } catch (Exception e) {
                System.err.println("操作失败: " + e.getMessage());
                ConsoleUtil.pressAnyKeyToContinue();
            }
        }
    }

    /**
     * 显示管理员菜单
     */
    private void displayAdminMenu() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 管理员管理系统 =============");
        System.out.println("当前用户: " + currentUser.getUsername());
        System.out.println("========================================");
        System.out.println("1. 审批新用户");
        System.out.println("2. 添加新用户");
        System.out.println("3. 删除用户");
        System.out.println("4. 查看所有用户");
        System.out.println("5. 查看待审核用户");
        System.out.println("6. 修改密码");
        System.out.println("0. 退出登录");
        System.out.println("========================================");
        System.out.print("请选择操作 (0-6): ");
    }

    /**
     * 处理用户审批
     */
    private void handleUserApproval() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 用户审批 =============");

        // 获取待审核用户列表
        List<User> pendingUsers = userDao.getPendingUsers();

        if (pendingUsers.isEmpty()) {
            System.out.println("没有待审核的用户！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // 显示待审核用户列表
        displayUserList(pendingUsers, "待审核用户列表");

        System.out.print("请输入要审批的用户ID (输入0返回): ");
        int userId = getIntInput();

        if (userId == 0) return;

        // 查找用户
        User userToApprove = userDao.getUserById(userId);
        if (userToApprove == null || !"待审核".equals(userToApprove.getApprovalStatus())) {
            System.out.println("用户不存在或无需审批！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // 显示审批选项
        System.out.println("\n请选择审批结果:");
        System.out.println("1. 通过");
        System.out.println("2. 拒绝");
        System.out.println("3. 取消");
        System.out.print("请选择 (1-3): ");

        int approvalChoice = getIntInput();

        switch (approvalChoice) {
            case 1: // 通过
                if (userDao.updateApprovalStatus(userId, "已通过")) {
                    System.out.println("✅ 用户 " + userToApprove.getUsername() + " 已通过审核！");
                }
                break;
            case 2: // 拒绝
                if (userDao.updateApprovalStatus(userId, "已拒绝")) {
                    System.out.println("❌ 用户 " + userToApprove.getUsername() + " 审核未通过！");
                }
                break;
            case 3: // 取消
                System.out.println("操作已取消");
                break;
            default:
                System.out.println("无效的选择！");
        }

        ConsoleUtil.pressAnyKeyToContinue();
    }

    /**
     * 处理添加用户
     */
    private void handleAddUser() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 添加新用户 =============");

        System.out.print("请输入用户名: ");
        String username = scanner.nextLine().trim();

        // 检查用户名是否存在
        if (userDao.getUserByUsername(username) != null) {
            System.out.println("❌ 用户名已存在！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        System.out.print("请输入密码: ");
        String password = scanner.nextLine().trim();

        System.out.print("请再次输入密码: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            System.out.println("❌ 两次输入的密码不一致！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // 选择用户类型
        System.out.println("\n请选择用户类型:");
        System.out.println("1. 类型1用户");
        System.out.println("2. 类型2用户");
        System.out.println("3. 取消");
        System.out.print("请选择 (1-3): ");

        int typeChoice = getIntInput();

        String userType;
        switch (typeChoice) {
            case 1:
                userType = "类型1";
                break;
            case 2:
                userType = "类型2";
                break;
            case 3:
                System.out.println("操作已取消");
                return;
            default:
                System.out.println("无效的选择！");
                return;
        }

        // 创建用户对象
        User newUser = new User(username, password, userType);
        newUser.setApprovalStatus("已通过"); // 管理员直接添加的用户默认通过

        // 保存到数据库
        if (userDao.addUser(newUser)) {
            System.out.println("✅ 用户添加成功！");
        } else {
            System.out.println("❌ 用户添加失败！");
        }

        ConsoleUtil.pressAnyKeyToContinue();
    }

    /**
     * 处理删除用户
     */
    private void handleDeleteUser() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 删除用户 =============");

        // 获取所有用户（排除当前管理员自己）
        List<User> allUsers = userDao.getAllUsers();

        if (allUsers.isEmpty()) {
            System.out.println("没有可删除的用户！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // 显示用户列表
        displayUserList(allUsers, "所有用户列表");

        System.out.print("请输入要删除的用户ID (输入0返回): ");
        int userId = getIntInput();

        if (userId == 0) return;

        // 不能删除自己
        if (userId == currentUser.getUserId()) {
            System.out.println("❌ 不能删除自己的账号！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // 确认删除
        System.out.print("确定要删除这个用户吗？(y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            if (userDao.deleteUser(userId)) {
                System.out.println("✅ 用户删除成功！");
            } else {
                System.out.println("❌ 用户删除失败！");
            }
        } else {
            System.out.println("操作已取消");
        }

        ConsoleUtil.pressAnyKeyToContinue();
    }

    /**
     * 处理查看用户列表
     */
    private void handleViewUsers() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 用户列表 =============");

        // 获取所有用户
        List<User> allUsers = userDao.getAllUsers();

        if (allUsers.isEmpty()) {
            System.out.println("暂无用户数据！");
        } else {
            displayUserList(allUsers, "所有用户信息");
        }

        ConsoleUtil.pressAnyKeyToContinue();
    }

    /**
     * 处理查看待审核用户
     */
    private void handleViewPendingUsers() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 待审核用户 =============");

        // 获取待审核用户
        List<User> pendingUsers = userDao.getPendingUsers();

        if (pendingUsers.isEmpty()) {
            System.out.println("暂无待审核用户！");
        } else {
            displayUserList(pendingUsers, "待审核用户列表");
        }

        ConsoleUtil.pressAnyKeyToContinue();
    }

    /**
     * 处理修改密码
     */
    private void handleChangePassword() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 修改密码 =============");

        System.out.print("请输入当前密码: ");
        String currentPassword = scanner.nextLine().trim();

        System.out.print("请输入新密码: ");
        String newPassword = scanner.nextLine().trim();

        System.out.print("请再次输入新密码: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("❌ 两次输入的新密码不一致！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // TODO: 这里应该调用AuthService的修改密码方法
        // 暂时只显示消息
        System.out.println("✅ 密码修改功能开发中...");
        ConsoleUtil.pressAnyKeyToContinue();
    }

    /**
     * 显示用户列表
     */
    private void displayUserList(List<User> users, String title) {
        System.out.println("=== " + title + " ===");
        System.out.println("ID\t用户名\t\t类型\t状态\t创建时间");
        System.out.println("----------------------------------------------------");

        for (User user : users) {
            System.out.printf("%d\t%-12s\t%s\t%s\t%s\n",
                    user.getUserId(),
                    user.getUsername(),
                    user.getUserType(),
                    user.getApprovalStatus(),
                    user.getCreatedTime());
        }
        System.out.println("----------------------------------------------------");
        System.out.println("共 " + users.size() + " 个用户");
    }

    /**
     * 获取用户输入的数字
     */
    private int getIntInput() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // 表示无效输入
        }
    }

    /**
     * 获取用户选择
     */
    private int getUserChoice() {
        return getIntInput();
    }
}