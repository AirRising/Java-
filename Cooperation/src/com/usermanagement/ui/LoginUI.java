package com.usermanagement.ui;

import com.usermanagement.model.User;
import com.usermanagement.service.AuthService;
import com.usermanagement.service.AuthServiceImpl;
import com.usermanagement.util.ConsoleUtil;
import java.util.Scanner;

/**
 * 登录界面
 */
public class LoginUI {
    private Scanner scanner;
    private AuthService authService;
    private User currentUser;

    public LoginUI() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthServiceImpl();
    }

    /**
     * 显示登录界面
     */
    public void show() {
        while (true) {
            try {
                displayLoginMenu();
                int choice = getUserChoice();

                switch (choice) {
                    case 1: // 登录
                        handleLogin();
                        break;
                    case 2: // 注册
                        handleRegister();
                        break;
                    case 3: // 退出系统
                        System.out.println("感谢使用，再见！");
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
     * 显示登录菜单
     */
    private void displayLoginMenu() {
        ConsoleUtil.clearScreen();
        System.out.println("============== 用户登录系统 ==============");
        System.out.println("1. 登录");
        System.out.println("2. 注册");
        System.out.println("3. 退出系统");
        System.out.println("========================================");
        System.out.print("请选择操作 (1-3): ");
    }

    /**
     * 处理用户登录
     */
    private void handleLogin() {
        ConsoleUtil.clearScreen();
        System.out.println("============== 用户登录 ==============");

        // 选择用户类型（模拟下拉列表）
        String userType = selectUserType();
        if (userType == null) return;

        // 输入用户名和密码
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine().trim();

        System.out.print("请输入密码: ");
        String password = scanner.nextLine().trim();

        // 验证登录
        try {
            currentUser = authService.login(username, password, userType);

            if (currentUser != null) {
                System.out.println("\n✅ 登录成功！");
                System.out.println("欢迎，" + currentUser.getUsername() + "！");
                ConsoleUtil.pressAnyKeyToContinue();

                // 根据用户类型跳转到不同界面
                redirectToUserInterface();
            } else {
                System.out.println("\n❌ 登录失败，用户名或密码错误！");
                ConsoleUtil.pressAnyKeyToContinue();
            }

        } catch (Exception e) {
            System.out.println("\n❌ " + e.getMessage());
            ConsoleUtil.pressAnyKeyToContinue();
        }
    }

    /**
     * 选择用户类型
     */
    private String selectUserType() {
        System.out.println("请选择用户类型:");
        System.out.println("1. 管理员");
        System.out.println("2. 类型1用户");
        System.out.println("3. 类型2用户");
        System.out.println("0. 返回上级");
        System.out.print("请选择 (0-3): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1: return User.TYPE_ADMIN;
                case 2: return User.TYPE_USER1;
                case 3: return User.TYPE_USER2;
                case 0: return null;
                default:
                    System.out.println("无效的选择！");
                    return selectUserType();
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字！");
            return selectUserType();
        }
    }

    /**
     * 跳转到用户界面
     */
    private void redirectToUserInterface() {
        if (currentUser.isAdmin()) {
            // 跳转到管理员界面
            AdminUI adminUI = new AdminUI(currentUser);
            adminUI.show();
        } else {
            // 跳转到普通用户界面
            UserUI userUI = new UserUI(currentUser);
            userUI.show();
        }
    }

    /**
     * 处理用户注册
     */
    private void handleRegister() {
        // 跳转到注册界面
        RegisterUI registerUI = new RegisterUI();
        registerUI.show();
    }

    /**
     * 获取用户选择
     */
    private int getUserChoice() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // 表示无效输入
        }
    }
}