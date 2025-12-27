package com.usermanagement.ui;

import com.usermanagement.model.User;
import com.usermanagement.service.AuthService;
import com.usermanagement.service.AuthServiceImpl;
import com.usermanagement.util.ConsoleUtil;

import java.util.Scanner;

/**
 * 用户注册界面
 */
public class RegisterUI {
    private Scanner scanner;
    private AuthService authService;

    public RegisterUI() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthServiceImpl();
    }

    /**
     * 显示注册界面
     */
    public void show() {
        while (true) {
            try {
                displayRegisterMenu();
                int choice = getUserChoice();

                switch (choice) {
                    case 1: // 注册类型1用户
                        handleRegister("类型1");
                        break;
                    case 2: // 注册类型2用户
                        handleRegister("类型2");
                        break;
                    case 0: // 返回登录界面
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
     * 显示注册菜单
     */
    private void displayRegisterMenu() {
        ConsoleUtil.clearScreen();
        System.out.println("============== 用户注册 ==============");
        System.out.println("1. 注册类型1用户");
        System.out.println("2. 注册类型2用户");
        System.out.println("0. 返回登录界面");
        System.out.println("=====================================");
        System.out.print("请选择注册类型 (0-2): ");
    }

    /**
     * 处理用户注册
     */
    private void handleRegister(String userType) {
        ConsoleUtil.clearScreen();
        System.out.println("============== 注册" + userType + " ==============");

        // 输入用户名
        System.out.print("请输入用户名 (至少3个字符): ");
        String username = scanner.nextLine().trim();

        if (username.length() < 3) {
            System.out.println("❌ 用户名至少需要3个字符！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // 检查用户名是否已存在
        if (authService.isUsernameExists(username)) {
            System.out.println("❌ 用户名已存在，请使用其他用户名！");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // 输入密码
        System.out.print("请输入密码 (至少6位，包含字母和数字): ");
        String password = scanner.nextLine().trim();

        // 验证密码强度
        if (!authService.validatePasswordStrength(password)) {
            System.out.println("❌ 密码强度不足！要求：至少6位，包含字母和数字");
            ConsoleUtil.pressAnyKeyToContinue();
            return;
        }

        // 确认密码
        System.out.print("请再次输入密码: ");
        String confirmPassword = scanner.nextLine().trim();

        // 创建用户对象
        User newUser = new User(username, password, userType);

        // 调用注册服务
        if (authService.register(newUser, confirmPassword)) {
            System.out.println("\n✅ 注册成功！请等待管理员审核。");
            System.out.println("您的用户名: " + username);
            System.out.println("用户类型: " + userType);
            System.out.println("审核状态: 待审核");
        } else {
            System.out.println("\n❌ 注册失败，请重试！");
        }

        ConsoleUtil.pressAnyKeyToContinue();
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