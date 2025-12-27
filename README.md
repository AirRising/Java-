# **JAVA大作业说明**

## 用户管理系统数据库初始化脚本 - SQL Server版

### 🚀️ 测试平台

[OneCompile](https://onecompiler.com/sqlserver/)

### 📋项目概述

本SQL脚本为**用户管理系统**创建完整的数据库结构，适用于基于SQL Server的Java用户管理系统。脚本包含表结构、索引、视图、触发器、存储过程以及初始测试数据。

### 🗄️ 数据库信息

- **数据库名称**: UserManagementDB
- **适用数据库**: Microsoft SQL Server (2008及以上版本)
- **字符编码**: 默认数据库编码
- **创建方式**: 执行完整SQL脚本

### 📁 表结构说明

#### 1. 核心表

##### **Users (用户表)**

存储系统所有用户信息，包括管理员和普通用户。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| UserID | INT | 主键，自增 | 用户唯一标识 |
| Username | NVARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| Password | NVARCHAR(100) | NOT NULL | 密码（建议加密存储） |
| UserType | NVARCHAR(20) | CHECK约束 | 用户类型：管理员/类型1/类型2 |
| ApprovalStatus | NVARCHAR(20) | 默认'待审核' | 审核状态：待审核/已通过/已拒绝 |
| CreatedTime | DATETIME | 默认当前时间 | 创建时间 |
| LastLoginTime | DATETIME | NULL | 最后登录时间 |
| Remark | NVARCHAR(200) | NULL | 备注信息 |

##### **UserData (用户数据表)**

存储用户管理的数据信息，用于数据管理模块。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| DataID | INT | 主键，自增 | 数据唯一标识 |
| UserID | INT | 外键引用Users | 关联用户ID |
| DataType | NVARCHAR(50) | NOT NULL | 数据类型标识 |
| Field1-5 | NVARCHAR(100) | NULL | 通用字段，可存储不同类型数据 |
| CreatedTime | DATETIME | 默认当前时间 | 创建时间 |
| ModifiedTime | DATETIME | 默认当前时间 | 修改时间 |

##### **UserLogs (用户日志表)**

记录用户操作日志，通过触发器自动生成。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| LogID | INT | 主键，自增 | 日志唯一标识 |
| UserID | INT | 外键引用Users | 操作用户ID |
| Action | NVARCHAR(50) | NOT NULL | 操作类型 |
| Details | NVARCHAR(500) | NULL | 操作详情 |
| LogTime | DATETIME | 默认当前时间 | 操作时间 |

#### 🔧 数据库对象

##### 1. **索引**

- `idx_username` - 用户名索引
- `idx_user_type` - 用户类型索引
- `idx_approval_status` - 审核状态索引

##### 2. **视图**

- `ApprovedUsers` - 已通过审核的用户视图
- `PendingUsers` - 待审核的用户视图

##### 3. **触发器**

- `trg_UserStatusChange` - 用户状态变更触发器
  - 作用：当用户审核状态变更时自动记录日志
  - 触发时机：UPDATE操作后
  - 记录内容：状态变更详情

##### 4. **存储过程**

- `sp_GetPendingUsers` - 获取所有待审核用户
- `sp_UpdateUserStatus` - 更新用户审核状态

#### 📊 初始数据

##### 1. **预置管理员账号**

- 用户名：`admin`
- 密码：`admin123`（⚠️ 实际项目应加密存储）
- 用户类型：`管理员`
- 审核状态：`已通过`

##### 2. **测试用户数据**

| 用户名 | 密码 | 用户类型 | 审核状态 |
|--------|------|----------|----------|
| user1 | password123 | 类型1 | 已通过 |
| user2 | password123 | 类型2 | 待审核 |
| user3 | password123 | 类型1 | 待审核 |
| user4 | password123 | 类型2 | 已通过 |
| user5 | password123 | 类型1 | 已拒绝 |

#### 3. **测试用户数据**

包含3条示例用户数据，用于演示数据管理功能。

### 🚀 安装与部署

#### 方法一：使用SQL Server Management Studio (SSMS)

1. 打开SSMS并连接到目标SQL Server实例
2. 点击"新建查询"
3. 复制并粘贴完整脚本内容
4. 点击"执行"按钮（或按F5）

#### 方法二：使用命令行工具

```bash
# 使用sqlcmd工具执行脚本
sqlcmd -S [服务器名] -U [用户名] -P [密码] -i init_db.sql

# 示例（本地服务器）：
sqlcmd -S localhost -U sa -P your_password -i init_db.sql
```

#### 方法三：使用JDBC在Java中执行

```java
// 读取SQL文件并执行
Statement stmt = connection.createStatement();
String sql = new String(Files.readAllBytes(Paths.get("init_db.sql")));
stmt.execute(sql);
```

### 🔗 Java连接配置

### JDBC连接字符串

```java
```String url = "jdbc:sqlserver://localhost:1433;databaseName=UserManagementDB;encrypt=true;trustServerCertificate=true;";
String username = "sa";  // 您的SQL Server用户名
String password = "your_password";  // 您的SQL Server密码
```

### Maven依赖

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>9.4.1.jre11</version>
</dependency>
```

### 📝 使用说明

#### 1. 用户管理

```sql
-- 查询所有用户
SELECT * FROM Users;

-- 查询待审核用户
SELECT * FROM PendingUsers;

-- 查询已通过用户
SELECT * FROM ApprovedUsers;

-- 调用存储过程更新用户状态
EXEC sp_UpdateUserStatus @UserID = 2, @NewStatus = '已通过';
```

#### 2. 数据管理

```sql
-- 查询用户数据
SELECT u.Username, ud.* 
FROM Users u 
JOIN UserData ud ON u.UserID = ud.UserID;

-- 添加新数据
INSERT INTO UserData (UserID, DataType, Field1, Field2) 
VALUES (1, '个人信息', '张三', '男');
```

#### 3. 日志查看

```sql
-- 查看用户操作日志
SELECT * FROM UserLogs ORDER BY LogTime DESC;
```

### ⚠️ 注意事项

1. **密码安全**：脚本中的密码为明文，实际项目中应使用加密算法（如BCrypt）加密存储
2. **权限控制**：确保执行脚本的用户有足够的数据库权限
3. **备份数据**：执行脚本前建议备份现有数据库
4. **测试环境**：建议先在测试环境执行，确认无误后再在生产环境部署
5. **连接配置**：根据实际环境修改连接字符串中的服务器地址和认证信息

### 🔄 维护与更新

#### 定期维护任务

1. 定期清理UserLogs表中的旧日志
2. 监控数据库性能，必要时重建索引
3. 备份重要数据

#### 版本更新

如需更新数据库结构，请：

1. 创建变更脚本
2. 在测试环境验证
3. 备份生产环境数据
4. 执行变更脚本

### 🆘 故障排除

#### 常见问题

1. **连接失败**：检查SQL Server服务是否启动，防火墙设置
2. **权限不足**：确保登录用户有创建数据库和表的权限
3. **对象已存在**：脚本包含DROP语句，会删除现有同名对象，请确保已备份重要数据

#### 错误处理

- 如果执行过程中出现错误，请查看具体的错误信息
- 检查SQL Server版本兼容性
- 确保磁盘空间充足

---

**版本**: 1.0
**最后更新**: 2025年
**适用项目**: Java用户管理系统课程项目
**数据库要求**: SQL Server 2008及以上版本

## 用户管理系统 - 用户登录模块

### 📋 项目概述

这是一个基于Java控制台的用户管理系统，实现了完整的用户注册、登录、权限管理和数据管理功能。项目采用分层架构设计，使用SQL Server作为数据库，实现了管理员和普通用户的双重权限体系。

### ✨ 功能特性

#### 🔐 用户认证模块

- **用户注册**：支持两种用户类型注册，二次密码验证
- **用户登录**：角色选择登录，权限验证
- **密码安全**：密码加密存储，安全验证

#### 👨‍💼 管理员功能

- **用户审批**：审核新注册用户
- **用户管理**：添加、删除用户
- **用户查询**：查看所有用户信息

#### 📊 数据管理模块

- **CRUD操作**：增删改查完整实现
- **灵活查询**：单字段/多字段联合查询
- **触发器联动**：用户操作自动记录

### 🛠️ 技术栈

#### 后端技术

- **语言**：Java 8+
- **数据库**：SQL Server 2008+
- **JDBC驱动**：Microsoft SQL Server JDBC Driver
- **架构模式**：MVC分层架构

#### 开发工具

- **IDE**：IntelliJ IDEA
- **版本控制**：Git
- **构建工具**：纯Java项目（无Maven/Gradle）

### 📁 项目结构

```
Cooperation/
├── src/                             # 源代码目录
│   ├── config.properties            # 配置文件
│   └── com/
│       └── usermanagement/          # 主包
│           ├── Main.java            # 程序入口
│           ├── model/               # 数据模型层
│           │   └── User.java        # 用户实体类
│           ├── dao/                 # 数据访问层
│           │   ├── UserDao.java     # 用户DAO接口
│           │   └── UserDaoImpl.java # 用户DAO实现
│           ├── service/             # 业务逻辑层
│           │   └── AuthService.java # 认证服务接口
│           ├── ui/                  # 用户界面层
│           │   └── LoginUI.java     # 登录界面
│           ├── util/                # 工具类
│           │   ├── ConsoleUtil.java # 控制台工具
│           │   ├── DatabaseUtil.java # 数据库工具
│           │   └── PasswordUtil.java # 密码工具
│           └── config/              # 配置类
│               └── DatabaseConfig.java # 数据库配置
├── lib/                             # 第三方库
│   └── mssql-jdbc.jar               # SQL Server JDBC驱动
├── sql/                             # SQL脚本
│   └── init_db.sql                  # 数据库初始化脚本
├── .gitignore                       # Git忽略文件
└── README.md                        # 项目说明文档
```

### 🚀 快速开始

#### 环境要求

1. **Java环境**：JDK 8或更高版本
2. **数据库**：SQL Server 2008或更高版本
3. **IDE**：IntelliJ IDEA（推荐）或Eclipse

#### 安装步骤

##### 步骤1：克隆项目

```bash
git clone [项目地址]
cd Cooperation
```

##### 步骤2：数据库设置

1. 打开SQL Server Management Studio
2. 执行 `sql/init_db.sql` 脚本创建数据库
3. 修改数据库连接配置（见步骤3）

##### 步骤3：配置数据库连接

编辑 `src/config.properties` 文件：

```properties
# 数据库配置
db.url=jdbc:sqlserver://localhost:1433;databaseName=UserManagementDB;encrypt=true;trustServerCertificate=true;
db.username=sa
db.password=your_password_here
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver

# 应用配置
app.name=用户管理系统
app.version=1.0
```

##### 步骤4：添加JDBC驱动

1. 下载 [SQL Server JDBC驱动](https://docs.microsoft.com/zh-cn/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server)
2. 将 `mssql-jdbc.jar` 复制到 `lib/` 目录
3. 在IDEA中添加为库依赖

#### 步骤5：运行项目

在IntelliJ IDEA中：

1. 打开项目
2. 右键点击 `Main.java`
3. 选择 **Run 'Main.main()'**

或在命令行中：

```bash
# 编译
javac -d bin -cp "lib/*" src/com/usermanagement/*.java src/com/usermanagement/**/*.java

# 运行
java -cp "bin;lib/*" com.usermanagement.Main
```

### 📖 使用说明

#### 登录系统

1. 运行程序后，进入登录界面
2. 选择用户类型（管理员/类型1/类型2）
3. 输入用户名和密码
4. 根据权限进入相应界面

#### 管理员功能

- **默认管理员账号**：admin / admin123
- **用户审批**：查看待审核用户并审批
- **用户管理**：添加、删除用户
- **用户查询**：查看所有用户信息

#### 普通用户功能

- **数据管理**：对分配的数据进行增删改查
- **查询功能**：支持多字段联合查询
- **数据导出**：查询结果可导出

### 🔧 开发指南

#### 代码规范

- 遵循Java命名规范
- 使用有意义的变量名和方法名
- 添加必要的注释

#### 包结构说明

- **model**：数据模型，与数据库表对应
- **dao**：数据访问对象，处理数据库操作
- **service**：业务逻辑层，处理业务规则
- **ui**：用户界面，控制台交互
- **util**：工具类，提供通用功能
- **config**：配置类，管理应用配置

#### 扩展功能

1. **添加新用户类型**：修改User类中的常量
2. **扩展数据字段**：修改UserData表结构
3. **添加新功能模块**：按现有模式添加新的service和dao

### 🐛 故障排除

#### 常见问题

##### 1. 数据库连接失败

```
错误：java.sql.SQLException: The connection to the host ... failed.
```

**解决方案**：

- 检查SQL Server服务是否启动
- 验证连接字符串中的服务器地址和端口
- 检查防火墙设置

##### 2. 配置文件找不到

```
错误：java.io.FileNotFoundException: config.properties
```

**解决方案**：

- 确保 `config.properties` 在 `src` 目录下
- 检查文件编码（应为UTF-8）
- 重新标记 `src` 为源目录

##### 3. 缺少JDBC驱动

```
错误：java.lang.ClassNotFoundException: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

**解决方案**：

- 确认 `mssql-jdbc.jar` 已添加到类路径
- 在IDEA中重新添加库依赖

##### 4. 权限不足

```
错误：用户登录失败或权限不足
```

**解决方案**：

- 检查用户是否已通过审核
- 验证用户名和密码
- 确认用户类型选择正确

##### 调试建议

1. **启用详细日志**：在DatabaseConfig中增加日志输出
2. **检查数据库状态**：直接在SSMS中测试SQL语句
3. **单元测试**：为关键方法添加测试用例

#### 📊 数据库设计

##### 主要表结构

###### Users表（用户表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| UserID | INT | 主键，自增长 |
| Username | NVARCHAR(50) | 用户名，唯一 |
| Password | NVARCHAR(100) | 加密密码 |
| UserType | NVARCHAR(20) | 用户类型 |
| ApprovalStatus | NVARCHAR(20) | 审核状态 |
| CreatedTime | DATETIME | 创建时间 |
| LastLoginTime | DATETIME | 最后登录时间 |

##### UserData表（用户数据表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| DataID | INT | 主键，自增长 |
| UserID | INT | 外键，关联用户 |
| DataType | NVARCHAR(50) | 数据类型 |
| Field1-5 | NVARCHAR(100) | 通用数据字段 |
| CreatedTime | DATETIME | 创建时间 |
| ModifiedTime | DATETIME | 修改时间 |

### 🤝 合作开发

#### 分支策略

- **main**：主分支，稳定版本
- **develop**：开发分支，集成最新功能
- **feature/**：功能分支，开发新功能
- **hotfix/**：热修复分支，紧急修复

#### 提交规范

```
类型(范围): 描述

[详细说明]

[相关Issue]
```

**类型说明**：

- feat：新功能
- fix：错误修复
- docs：文档更新
- style：代码格式
- refactor：代码重构
- test：测试相关

#### 代码审查

1. 所有代码需经过review
2. 确保测试通过
3. 遵循编码规范

### 📝 版本历史

#### v1.0.0 (2025-12-28)

- ✅ 用户注册登录功能
- ✅ 管理员审批系统
- ✅ 基本数据管理功能
- ✅ SQL Server数据库集成
- ✅ 控制台用户界面

### 📄 许可证

本项目仅用于课程学习和演示，保留所有权利。

### 📞 支持与联系

#### 开发团队

- **项目经理**：[姓名]
- **后端开发**：[姓名]
- **数据库设计**：[姓名]
- **测试**：[姓名]
---

**最后更新**：2025年12月27日
**项目状态**：等待B开发中

