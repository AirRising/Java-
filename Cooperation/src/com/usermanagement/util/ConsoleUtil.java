package com.usermanagement.util;

import java.util.Scanner;

/**
 * 控制台工具类
 */
public class ConsoleUtil {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * 清空控制台屏幕
     */
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // 如果清屏失败，打印多行空行
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * 显示分隔线
     */
    public static void printSeparator() {
        System.out.println("========================================");
    }

    /**
     * 显示标题
     */
    public static void printTitle(String title) {
        printSeparator();
        System.out.println("         " + title);
        printSeparator();
    }

    /**
     * 按任意键继续
     */
    public static void pressAnyKeyToContinue() {
        System.out.print("\n按任意键继续...");
        scanner.nextLine();
    }

    /**
     * 获取整数输入
     */
    public static int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字！");
            return getIntInput(prompt);
        }
    }

    /**
     * 获取字符串输入
     */
    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}