package com.usermanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import com.usermanagement.config.DatabaseConfig;

/**
 * 数据库工具类（单例模式）
 */
public class DatabaseUtil {
    private static Connection connection = null;
    private static final Object lock = new Object();

    // 私有构造方法
    private DatabaseUtil() {}

    /**
     * 初始化数据库连接
     */
    public static void initialize() throws SQLException {
        synchronized (lock) {
            if (connection == null || connection.isClosed()) {
                try {
                    // 加载JDBC驱动
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                    // 创建连接
                    connection = DriverManager.getConnection(
                            DatabaseConfig.getUrl(),
                            DatabaseConfig.getUsername(),
                            DatabaseConfig.getPassword()
                    );

                    System.out.println("数据库连接成功！");

                } catch (ClassNotFoundException e) {
                    throw new SQLException("SQL Server JDBC驱动未找到", e);
                }
            }
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initialize();
        }
        return connection;
    }

    /**
     * 关闭数据库连接
     */
    public static void close() {
        synchronized (lock) {
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("数据库连接已关闭");
                } catch (SQLException e) {
                    System.err.println("关闭数据库连接时出错: " + e.getMessage());
                }
                connection = null;
            }
        }
    }

    /**
     * 执行查询
     */
    public static ResultSet executeQuery(String sql) throws SQLException {
        Statement stmt = getConnection().createStatement();
        return stmt.executeQuery(sql);
    }

    /**
     * 执行更新（增删改）
     */
    public static int executeUpdate(String sql) throws SQLException {
        Statement stmt = getConnection().createStatement();
        return stmt.executeUpdate(sql);
    }

    /**
     * 开始事务
     */
    public static void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    /**
     * 提交事务
     */
    public static void commitTransaction() throws SQLException {
        getConnection().commit();
        getConnection().setAutoCommit(true);
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("回滚事务失败: " + e.getMessage());
        }
    }
}