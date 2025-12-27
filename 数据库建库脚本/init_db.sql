-- 创建数据库（如果不存在）
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'UserManagementDB')
BEGIN
    CREATE DATABASE UserManagementDB;
    PRINT '数据库 UserManagementDB 创建成功';
END
GO

USE UserManagementDB;
GO

-- 创建用户表
IF OBJECT_ID('Users', 'U') IS NOT NULL
    DROP TABLE Users;
GO

CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(100) NOT NULL,
    UserType NVARCHAR(20) NOT NULL CHECK (UserType IN ('管理员', '类型1', '类型2')),
    ApprovalStatus NVARCHAR(20) NOT NULL DEFAULT '待审核' 
        CHECK (ApprovalStatus IN ('待审核', '已通过', '已拒绝')),
    CreatedTime DATETIME DEFAULT GETDATE(),
    LastLoginTime DATETIME,
    Remark NVARCHAR(200) NULL
);
GO

-- 预置管理员账号（密码：admin123）
-- 注意：实际项目中密码应该加密存储，这里为了演示使用明文
INSERT INTO Users (Username, Password, UserType, ApprovalStatus) 
VALUES (N'admin', N'admin123', N'管理员', N'已通过');
GO

-- 创建索引以提高查询效率
CREATE INDEX idx_username ON Users(Username);
CREATE INDEX idx_user_type ON Users(UserType);
CREATE INDEX idx_approval_status ON Users(ApprovalStatus);
GO

-- 创建视图，方便查看不同类型的用户
IF OBJECT_ID('ApprovedUsers', 'V') IS NOT NULL
    DROP VIEW ApprovedUsers;
GO

CREATE VIEW ApprovedUsers AS
SELECT * FROM Users WHERE ApprovalStatus = '已通过';
GO

IF OBJECT_ID('PendingUsers', 'V') IS NOT NULL
    DROP VIEW PendingUsers;
GO

CREATE VIEW PendingUsers AS
SELECT * FROM Users WHERE ApprovalStatus = '待审核';
GO

-- 创建数据表用于存储用户数据（数据管理模块使用）
IF OBJECT_ID('UserData', 'U') IS NOT NULL
    DROP TABLE UserData;
GO

CREATE TABLE UserData (
    DataID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    DataType NVARCHAR(50) NOT NULL,
    Field1 NVARCHAR(100),
    Field2 NVARCHAR(100),
    Field3 NVARCHAR(100),
    Field4 NVARCHAR(100),
    Field5 NVARCHAR(100),
    CreatedTime DATETIME DEFAULT GETDATE(),
    ModifiedTime DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);
GO

-- 创建触发器示例：当用户状态更新时记录日志
IF OBJECT_ID('trg_UserStatusChange', 'TR') IS NOT NULL
    DROP TRIGGER trg_UserStatusChange;
GO

CREATE TRIGGER trg_UserStatusChange
ON Users
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- 检查审核状态是否变化
    IF UPDATE(ApprovalStatus)
    BEGIN
        INSERT INTO UserLogs (UserID, Action, Details, LogTime)
        SELECT 
            i.UserID,
            '状态变更',
            '用户状态从 "' + d.ApprovalStatus + '" 变更为 "' + i.ApprovalStatus + '"',
            GETDATE()
        FROM inserted i
        INNER JOIN deleted d ON i.UserID = d.UserID
        WHERE i.ApprovalStatus <> d.ApprovalStatus;
    END
END;
GO

-- 创建日志表（用于触发器记录）
IF OBJECT_ID('UserLogs', 'U') IS NOT NULL
    DROP TABLE UserLogs;
GO

CREATE TABLE UserLogs (
    LogID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    Action NVARCHAR(50) NOT NULL,
    Details NVARCHAR(500),
    LogTime DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- 插入测试数据（普通用户）
INSERT INTO Users (Username, Password, UserType, ApprovalStatus) VALUES
(N'user1', N'password123', N'类型1', N'已通过'),
(N'user2', N'password123', N'类型2', N'待审核'),
(N'user3', N'password123', N'类型1', N'待审核'),
(N'user4', N'password123', N'类型2', N'已通过'),
(N'user5', N'password123', N'类型1', N'已拒绝');
GO

-- 插入测试数据（用户数据）
INSERT INTO UserData (UserID, DataType, Field1, Field2, Field3) VALUES
(1, N'个人信息', N'张三', N'男', N'25岁'),
(2, N'个人信息', N'李四', N'女', N'30岁'),
(4, N'个人信息', N'王五', N'男', N'28岁');
GO

-- 创建存储过程：获取待审核用户列表
IF OBJECT_ID('sp_GetPendingUsers', 'P') IS NOT NULL
    DROP PROCEDURE sp_GetPendingUsers;
GO

CREATE PROCEDURE sp_GetPendingUsers
AS
BEGIN
    SELECT 
        UserID,
        Username,
        UserType,
        CreatedTime
    FROM Users
    WHERE ApprovalStatus = '待审核'
    ORDER BY CreatedTime;
END;
GO

-- 创建存储过程：更新用户状态
IF OBJECT_ID('sp_UpdateUserStatus', 'P') IS NOT NULL
    DROP PROCEDURE sp_UpdateUserStatus;
GO

CREATE PROCEDURE sp_UpdateUserStatus
    @UserID INT,
    @NewStatus NVARCHAR(20)
AS
BEGIN
    IF @NewStatus IN ('已通过', '已拒绝')
    BEGIN
        UPDATE Users 
        SET ApprovalStatus = @NewStatus
        WHERE UserID = @UserID;
        
        SELECT '成功' AS Result, '状态已更新' AS Message;
    END
    ELSE
    BEGIN
        SELECT '失败' AS Result, '无效的状态值' AS Message;
    END
END;
GO

-- 验证数据
PRINT '=== 用户表数据 ===';
SELECT * FROM Users;
PRINT '';

PRINT '=== 待审核用户 ===';
SELECT * FROM PendingUsers;
PRINT '';

PRINT '=== 已通过用户 ===';
SELECT * FROM ApprovedUsers;
PRINT '';

-- 显示表结构信息
PRINT '=== 表结构信息 ===';
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('Users', 'UserData', 'UserLogs')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
