package com.usermanagement.ui;

import com.usermanagement.model.User;
import com.usermanagement.util.ConsoleUtil;

import java.util.Scanner;

/**
 * 普通用户界面
 */
public class UserUI {
    private Scanner scanner;
    private User currentUser;

    public UserUI(User currentUser) {
        this.scanner = new Scanner(System.in);
        this.currentUser = currentUser;
    }

    /**
     * 显示用户主菜单
     */
    public void show() {
        while (true) {
            try {
                displayUserMenu();
                int choice = getUserChoice();

                switch (choice) {
                    case 1: // 数据管理
                        handleDataManagement();
                        break;
                    case 2: // 个人信息
                        handleProfile();
                        break;
                    case 3: // 修改密码
                        handleChangePassword();
                        break;
                    case 0: // 退出登录
                        System.out.println("已退出用户系统");
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
     * 显示用户菜单
     */
    private void displayUserMenu() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 用户管理系统 =============");
        System.out.println("当前用户: " + currentUser.getUsername());
        System.out.println("用户类型: " + currentUser.getUserType());
        System.out.println("========================================");
        System.out.println("1. 数据管理");
        System.out.println("2. 个人信息");
        System.out.println("3. 修改密码");
        System.out.println("0. 退出登录");
        System.out.println("========================================");
        System.out.print("请选择操作 (0-3): ");
    }

    /**
     * 处理数据管理
     */
    private void handleDataManagement() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 数据管理 =============");
        System.out.println("数据管理功能开发中...");
        ConsoleUtil.pressAnyKeyToContinue();
    }

    /**
     * 处理个人信息
     */
    private void handleProfile() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 个人信息 =============");
        System.out.println("用户ID: " + currentUser.getUserId());
        System.out.println("用户名: " + currentUser.getUsername());
        System.out.println("用户类型: " + currentUser.getUserType());
        System.out.println("审核状态: " + currentUser.getApprovalStatus());
        System.out.println("注册时间: " + currentUser.getCreatedTime());
        System.out.println("最后登录: " + currentUser.getLastLoginTime());
        ConsoleUtil.pressAnyKeyToContinue();
    }

    /**
     * 处理修改密码
     */
    private void handleChangePassword() {
        ConsoleUtil.clearScreen();
        System.out.println("============= 修改密码 =============");
        System.out.println("修改密码功能开发中...");
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