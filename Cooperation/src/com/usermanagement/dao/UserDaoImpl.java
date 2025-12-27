package com.usermanagement.dao;

import com.usermanagement.model.User;
import com.usermanagement.util.DatabaseUtil;
import com.usermanagement.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (Username, Password, UserType, ApprovalStatus) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, PasswordUtil.encrypt(user.getPassword()));
            pstmt.setString(3, user.getUserType());
            pstmt.setString(4, user.getApprovalStatus());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("添加用户失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE UserID = ?";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("删除用户失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET Username = ?, Password = ?, UserType = ?, " +
                "ApprovalStatus = ?, LastLoginTime = ?, Remark = ? WHERE UserID = ?";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, PasswordUtil.encrypt(user.getPassword()));
            pstmt.setString(3, user.getUserType());
            pstmt.setString(4, user.getApprovalStatus());

            if (user.getLastLoginTime() != null) {
                pstmt.setTimestamp(5, new Timestamp(user.getLastLoginTime().getTime()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }

            pstmt.setString(6, user.getRemark());
            pstmt.setInt(7, user.getUserId());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("更新用户失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE UserID = ?";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户失败: " + e.getMessage());
        }

        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE Username = ?";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户失败: " + e.getMessage());
        }

        return null;
    }

    @Override
    public User validateLogin(String username, String password, String userType) {
        String sql = "SELECT * FROM Users WHERE Username = ? AND UserType = ?";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, userType);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("Password");
                    String inputPassword = PasswordUtil.encrypt(password);

                    if (inputPassword.equals(storedPassword)) {
                        User user = extractUserFromResultSet(rs);

                        // 检查审核状态
                        if (User.TYPE_ADMIN.equals(userType) ||
                                User.STATUS_APPROVED.equals(user.getApprovalStatus())) {
                            updateLoginTime(user.getUserId());
                            return user;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("登录验证失败: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY CreatedTime DESC";

        try (Statement stmt = DatabaseUtil.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("获取所有用户失败: " + e.getMessage());
        }

        return users;
    }

    @Override
    public List<User> getUsersByType(String userType) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE UserType = ? ORDER BY CreatedTime DESC";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, userType);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("获取类型为 " + userType + " 的用户失败: " + e.getMessage());
        }

        return users;
    }

    @Override
    public List<User> getPendingUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE ApprovalStatus = '待审核' ORDER BY CreatedTime";

        try (Statement stmt = DatabaseUtil.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("获取待审核用户失败: " + e.getMessage());
        }

        return users;
    }

    @Override
    public boolean updateLoginTime(int userId) {
        String sql = "UPDATE Users SET LastLoginTime = GETDATE() WHERE UserID = ?";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新登录时间失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateApprovalStatus(int userId, String status) {
        // 验证状态值是否合法
        if (!status.equals(User.STATUS_APPROVED) &&
                !status.equals(User.STATUS_REJECTED) &&
                !status.equals(User.STATUS_PENDING)) {
            System.err.println("无效的审核状态: " + status);
            return false;
        }

        String sql = "UPDATE Users SET ApprovalStatus = ? WHERE UserID = ?";

        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("用户ID " + userId + " 的审核状态已更新为: " + status);
                return true;
            } else {
                System.out.println("未找到用户ID: " + userId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("更新审核状态失败: " + e.getMessage());
            return false;
        }
    }

    // 辅助方法：从ResultSet提取User对象
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserID"));
        user.setUsername(rs.getString("Username"));
        user.setPassword(rs.getString("Password"));
        user.setUserType(rs.getString("UserType"));
        user.setApprovalStatus(rs.getString("ApprovalStatus"));
        user.setCreatedTime(rs.getTimestamp("CreatedTime"));
        user.setLastLoginTime(rs.getTimestamp("LastLoginTime"));
        user.setRemark(rs.getString("Remark"));
        return user;
    }
}