package com.usermanagement.model;

import java.util.Date;

/**
 * 用户实体类
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String userType;        // 用户类型：管理员/类型1/类型2
    private String approvalStatus;  // 审核状态：待审核/已通过/已拒绝
    private Date createdTime;
    private Date lastLoginTime;
    private String remark;

    // 用户类型常量
    public static final String TYPE_ADMIN = "管理员";
    public static final String TYPE_USER1 = "类型1";
    public static final String TYPE_USER2 = "类型2";

    // 审核状态常量
    public static final String STATUS_PENDING = "待审核";
    public static final String STATUS_APPROVED = "已通过";
    public static final String STATUS_REJECTED = "已拒绝";

    // 构造方法
    public User() {}

    public User(String username, String password, String userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.approvalStatus = STATUS_PENDING; // 默认待审核
    }

    // Getter 和 Setter 方法
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }

    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    // 工具方法
    public boolean isAdmin() {
        return TYPE_ADMIN.equals(userType);
    }

    public boolean isApproved() {
        return STATUS_APPROVED.equals(approvalStatus);
    }

    public boolean isPending() {
        return STATUS_PENDING.equals(approvalStatus);
    }

    @Override
    public String toString() {
        return String.format("用户ID: %d, 用户名: %s, 类型: %s, 状态: %s",
                userId, username, userType, approvalStatus);
    }
}
