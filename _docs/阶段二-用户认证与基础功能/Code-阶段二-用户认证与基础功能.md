# 阶段二代码文档（用户认证与基础功能）

## 1. 代码结构概览

- Android 客户端：`android/`
  - 认证模块：`android/app/src/main/java/com/wzh/suyuan/feature/auth/`
  - 主页面与导航：`android/app/src/main/java/com/wzh/suyuan/feature/main/`
  - 登录态存储：`android/app/src/main/java/com/wzh/suyuan/data/auth/AuthManager.java`
  - 网络层：`android/app/src/main/java/com/wzh/suyuan/network/`
- 后端服务：`backend/`
  - 认证接口与服务：`backend/src/main/java/com/wzh/suyuan/backend/controller/AuthController.java`
  - 认证服务与 JWT：`backend/src/main/java/com/wzh/suyuan/backend/service/AuthService.java`
  - 安全组件：`backend/src/main/java/com/wzh/suyuan/backend/security/`

## 2. Android 端实现说明

### 2.1 登录与注册

- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/auth/AuthActivity.java`
  - 登录/注册同页切换，注册时显示手机号输入
  - 表单校验与网络状态提示
  - 成功后进入 `MainActivity`
- 接口模型：`android/app/src/main/java/com/wzh/suyuan/network/model/`
  - `LoginRequest`、`RegisterRequest`、`LoginResponse`、`RegisterResponse`、`AuthUser`

### 2.2 登录态与 Token

- SP 存储：`android/app/src/main/java/com/wzh/suyuan/data/auth/AuthManager.java`
  - 保存 Token、过期时间与用户基础信息
  - 退出登录时清理本地登录态
- 请求拦截：
  - `HeaderInterceptor` 统一注入 `Authorization` 头
  - `AuthInterceptor` 捕获 401 并触发回到登录页

### 2.3 主页面与个人中心

- 主页面框架：`android/app/src/main/java/com/wzh/suyuan/feature/main/MainActivity.java`
  - 底部导航：首页 / 购物车 / 个人中心占位
  - 启动时自动请求 `/auth/me` 刷新用户信息
- 个人中心：`android/app/src/main/java/com/wzh/suyuan/feature/main/fragment/ProfileFragment.java`
  - 展示用户信息与版本信息
  - 支持确认退出登录

## 3. 后端实现说明

### 3.1 认证接口

- 控制器：`backend/src/main/java/com/wzh/suyuan/backend/controller/AuthController.java`
  - `POST /auth/register`：注册
  - `POST /auth/login`：登录并返回 Token
  - `GET /auth/me`：Token 校验与用户信息获取

### 3.2 JWT 与安全配置

- Token 生成与解析：`backend/src/main/java/com/wzh/suyuan/backend/security/JwtTokenProvider.java`
- 认证过滤器：`backend/src/main/java/com/wzh/suyuan/backend/security/JwtAuthenticationFilter.java`
- 安全配置：`backend/src/main/java/com/wzh/suyuan/backend/config/SecurityConfig.java`
  - 采用 JWT + BCrypt
  - `/auth/login`、`/auth/register`、`/health` 放行，其余接口需认证

### 3.3 统一响应与异常处理

- 响应模型：`backend/src/main/java/com/wzh/suyuan/backend/model/ApiResponse.java`
- 异常处理：`backend/src/main/java/com/wzh/suyuan/backend/exception/GlobalExceptionHandler.java`
  - 400 参数错误、401 未认证、409 用户已存在

## 4. 关键配置

- 后端 JWT 配置：`backend/src/main/resources/application.yml`
  - `security.jwt.secret`、`security.jwt.expiration-seconds`
- Android 接口基地址：`android/app/src/main/java/com/wzh/suyuan/network/NetworkConfig.java`

## 5. 测试说明

- 后端接口测试：`backend/src/test/java/com/wzh/suyuan/backend/AuthControllerTest.java`
  - 覆盖注册、登录、`/auth/me` 与无效 Token 场景

## 6. 运行建议

- Android：使用 Android Studio 打开 `android/`，运行 `MainActivity`，首次会进入登录页。
- 后端：在 `backend/` 执行 `mvn spring-boot:run`，确保数据库可用并配置好 `application.yml`。
