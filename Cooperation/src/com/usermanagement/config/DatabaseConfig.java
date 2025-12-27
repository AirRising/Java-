package com.usermanagement.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 数据库配置类
 */
public class DatabaseConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                // 使用默认配置
                setDefaultProperties();
            } else {
                props.load(input);
            }

        } catch (IOException e) {
            System.err.println("无法加载配置文件，使用默认配置");
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        props.setProperty("db.url", "jdbc:sqlserver://localhost:1433;databaseName=UserManagementDB;encrypt=true;trustServerCertificate=true;");
        props.setProperty("db.username", "sa");
        props.setProperty("db.password", "your_password");
        props.setProperty("db.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    public static String getUrl() {
        return props.getProperty("db.url");
    }

    public static String getUsername() {
        return props.getProperty("db.username");
    }

    public static String getPassword() {
        return props.getProperty("db.password");
    }

    public static String getDriver() {
        return props.getProperty("db.driver");
    }
}