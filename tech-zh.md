# 技术架构 - WhereAreTheyV2

## 概述
WhereAreTheyV2 是一款 Android 位置共享应用程序，使用户能够与朋友分享实时位置。该应用程序结合了原生 Android 开发、百度地图集成以及通过加密实现的安全数据处理。

## 架构图
```
┌─────────────────────────────────────────────────────────────┐
│                    Android 应用程序                         │
├─────────────────────────────────────────────────────────────┤
│  ┌───────────────┐  ┌───────────────┐  ┌─────────────────┐ │
│  │   UI 层       │  │ 业务逻辑层    │  │   数据层        │ │
│  │               │  │               │  │                 │ │
│  │ - Activities  │  │ - AsyncTasks  │  │ - DAOs          │ │
│  │ - Fragments   │  │ - Listeners   │  │ - Models        │ │
│  │ - 百度地图    │  │ - Utilities   │  │ - 加密          │ │
│  └───────────────┘  └───────────────┘  └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                    数据存储层                                │
├─────────────────────────────────────────────────────────────┤
│                      MariaDB 数据库                          │
└─────────────────────────────────────────────────────────────┘
```

## 前端架构

### 技术栈
- **语言**: Java
- **框架**: 原生 Android SDK
- **UI 组件**: Android Support Library (AppCompatActivity, Fragments 等)
- **地图服务**: 百度地图 SDK
- **最低 SDK**: API Level 16 (Android 4.1)
- **目标 SDK**: API Level 27 (Android 8.1)

### 主要组件

#### Activities
1. **MainActivity**: 启动屏幕，延迟导航到登录页面
2. **LoginActivity**: 用户认证和注册入口
3. **TabGroupActivity**: 带有标签导航的主应用程序界面
4. **辅助 Activities**: 
   - RegisterActivity
   - ForgetPwdActivity
   - ChangePwdActivity
   - SettingActivity
   - DisplayFriendsActivity
   - FriendsInfoActivity
   - TalkingRoomActivity

#### Fragments
1. **MessageFragment**: 显示用户间的消息
2. **LocationFragment**: 集成百度地图的核心位置共享功能
3. **MyInfoFragment**: 用户资料管理

#### UI 特性
- 基于标签的导航系统
- 朋友位置的地图可视化
- 用于不同用户状态（在线/离线）的自定义地图标记
- 带有点击处理程序的交互式地图元素
- 用于用户交互的自定义对话框

## 后端架构

### 数据存储
- **数据库**: MariaDB
- **连接方式**: JDBC
- **驱动**: mariadb-java-client
- **连接管理**: 具有连接池概念的自定义 DBConnection 类

### 数据访问层
- **UserDao**: 处理所有与用户相关的数据库操作
  - 用户认证
  - 用户注册
  - 密码管理
  - 资料更新
- **模型**:
  - User: 用户账户信息
  - Friends: 朋友关系
  - MessageIO: 用户间消息传递
  - NowLocation: 实时位置数据

### 安全层
- **加密**: AES 加密敏感数据
- **AesUtil**: 自定义加密工具类
- **数据保护**:
  - 存储前加密密码
  - 应用程序代码中加密敏感数据库凭证
  - 位置数据传输的端到端加密

## 核心功能流程

### 用户认证流程
1. 用户在 LoginActivity 中输入凭据
2. LoginTask 异步执行
3. UserDao 验证数据库中的凭据
4. 成功后，用户数据被解密并存储在 MainApplication 中
5. 用户导航到 TabGroupActivity

### 位置共享流程
1. LocationFragment 初始化百度地图和位置服务
2. 通过 BDLocationListener 捕获位置更新
3. SendLocationTask 定期传输加密的位置数据
4. GetFriendsLocationsTask 检索朋友的位置
5. 位置在地图上用自定义标记显示

### 消息传递流程
1. 用户通过地图标记点击或直接界面发起消息
2. SendMsgTask 将消息传输到数据库
3. 消息在 MessageFragment 中检索和显示

## 数据模型

### User
- userId: 唯一标识符
- password: AES 加密密码
- userName: 显示名称
- pwdProtectId: 密码恢复问题 ID
- pwdProtectA: AES 加密密码恢复答案
- userType: 用户角色/类型

### NowLocation
- userId: 关联用户
- latitude/longitude: GPS 坐标
- locationDesc: 人类可读的位置描述
- time: 位置更新时间戳

### MessageIO
- senderId: 消息发送者
- receiverId: 消息接收者
- content: 消息内容
- messageType: 消息类型

### Friends
- userId: 主用户
- friendId: 朋友的用户 ID
- 关系状态数据

## 安全实现

### 数据加密
- AES 256 位加密敏感数据
- 为 Android 7.0+ 兼容性定制加密提供程序
- 加密密钥存储在应用程序代码中（生产环境需要改进）

### 数据库安全
- 加密数据库连接凭证
- 预处理语句防止 SQL 注入
- 连接管理防止资源泄漏

### Android 权限
- 位置访问（精确定位和粗略定位）
- 网络状态监控
- 存储读写权限
- 手机状态访问

## 异步处理

### AsyncTask 实现
- **LoginTask**: 处理用户认证
- **SendLocationTask**: 传输位置数据
- **GetFriendsLocationsTask**: 检索朋友位置
- **ShowFriendsTask**: 获取朋友列表
- **SendMsgTask**: 处理消息传输

### 线程管理
- 用于定期位置更新的 Handler/Runnable
- 正确的 AsyncTask 生命周期管理
- Fragment 生命周期方法中的资源清理

## 第三方集成

### 百度地图 SDK
- 通过 LocationClient 提供位置服务
- 使用 BaiduMap 和 MapView 进行地图渲染
- 自定义标记和覆盖物
- 用于实时更新的位置监听器

### 数据库驱动
- 用于 JDBC 连接的 MariaDB Java Client

## 项目结构

```
app/
├── src/main/java/com/gjf/wherearethey_v2/
│   ├── bean/           # 数据模型
│   ├── databaseoperation/
│   │   ├── dao/        # 数据访问对象
│   │   └── dbconnection/ # 数据库连接管理
│   ├── encrypt/        # 加密工具
│   ├── fragment/       # UI 片段
│   ├── task/           # AsyncTask 实现
│   ├── util/           # 工具类
│   ├── MainActivity.java
│   ├── LoginActivity.java
│   ├── MainApplication.java
│   └── TabGroupActivity.java
├── src/main/res/       # 资源（布局、图片等）
└── libs/               # 外部库（JAR 文件）
```

## 性能考虑

### 内存管理
- MainApplication 的单例模式
- 生命周期方法中的正确资源清理
- 使用覆盖管理进行高效的地图渲染

### 网络效率
- 连接重用模式
- 异步操作防止 UI 阻塞
- 定期更新而非连续轮询

### 电池优化
- 可配置的位置更新频率
- 通过百度地图 SDK 进行 GPS 优化
- 位置跟踪的前台服务考虑

## 未来增强机会

1. **现代化**: 从 AsyncTask 迁移到现代并发（协程/RxJava）
2. **架构**: 实现 MVVM 或 MVP 模式以更好地分离关注点
3. **安全**: 改进生产环境的密钥存储机制
4. **网络**: 用 REST API 层替换直接 JDBC
5. **UI**: 使用 Material Design 组件现代化界面