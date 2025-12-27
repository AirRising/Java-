package com.usermanagement;

import com.usermanagement.ui.LoginUI;
import com.usermanagement.util.DatabaseUtil;

/**
 * 用户管理系统主程序入口
 */
public class Main {
    public static void main(String[] args) {
        try {
            // 1. 初始化数据库连接
            DatabaseUtil.initialize();

            // 2. 显示欢迎信息
            System.out.println("========================================");
            System.out.println("      欢迎使用用户管理系统 v1.0");
            System.out.println("========================================");

            // 3. 启动登录界面
            LoginUI loginUI = new LoginUI();
            loginUI.show();

        } catch (Exception e) {
            System.err.println("系统初始化失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 4. 关闭数据库连接
            DatabaseUtil.close();
        }
    }
}