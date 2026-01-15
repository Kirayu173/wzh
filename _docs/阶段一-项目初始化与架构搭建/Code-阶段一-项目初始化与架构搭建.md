# 阶段一代码文档（项目初始化与架构搭建）

## 1. 代码结构概览

- Android 客户端：`android/`
  - `app/`：应用入口、网络层、Room 数据库、示例 MVP 页面
  - `ui/`：MVP 基类与基础 Activity/Fragment
  - `kit/`：基础工具类（SP、网络状态、格式化、Toast、状态栏）
- 后端服务：`backend/`
  - Spring Boot 项目骨架、实体类、健康检查接口

## 2. Android 端模块说明

### 2.1 app 模块

- 应用入口：`android/app/src/main/java/com/wzh/suyuan/App.java`
  - 启动日志输出：版本号、构建类型、设备 API、网络类型
  - 初始化 Room 数据库与 Toast 工具
- MVP 示例页面：`android/app/src/main/java/com/wzh/suyuan/feature/main/`
  - `MainActivity` + `MainPresenter` + `MainContract`
  - 启动时触发：Room DAO 的最小查询 + `health` 网络请求示例
- 网络层：`android/app/src/main/java/com/wzh/suyuan/network/`
  - Retrofit + Fastjson + OkHttp
  - 超时、日志拦截器、请求头拦截器
  - `BaseResponse` 作为统一响应模型
- Room 数据库：`android/app/src/main/java/com/wzh/suyuan/data/db/`
  - `AppDatabase` + `BootstrapDao` + `BootstrapRecord`
  - 迁移占位 `MIGRATION_1_2`

### 2.2 ui 模块

- MVP 基类：`android/ui/src/main/java/com/wzh/suyuan/ui/mvp/`
  - `BaseView`、`IPresenter`、`IModel`
  - `BasePresenter`、`BaseModel`
- 基础页面：`android/ui/src/main/java/com/wzh/suyuan/ui/activity/base/BaseActivity.java`
- 基础 Fragment：`android/ui/src/main/java/com/wzh/suyuan/ui/fragment/BaseFragment.java`
- 通用宿主 Activity：`android/ui/src/main/java/com/wzh/suyuan/ui/activity/BaseFragmentActivity.java`

### 2.3 kit 模块

- SP 工具：`android/kit/src/main/java/com/wzh/suyuan/kit/SpUtils.java`
- 网络状态：`android/kit/src/main/java/com/wzh/suyuan/kit/NetworkUtils.java`
- 格式化工具：`android/kit/src/main/java/com/wzh/suyuan/kit/FormatUtils.java`
- Toast 工具：`android/kit/src/main/java/com/wzh/suyuan/kit/ToastUtils.java`
- 状态栏工具：`android/kit/src/main/java/com/wzh/suyuan/kit/StatusBarUtils.java`

## 3. 后端模块说明

- 启动类：`backend/src/main/java/com/wzh/suyuan/backend/SuyuanBackendApplication.java`
  - 启动日志输出：版本号、Profile、数据库 Host（脱敏）
- 健康检查：`backend/src/main/java/com/wzh/suyuan/backend/controller/HealthController.java`
  - `GET /health` 返回 `ApiResponse`
- 通用响应模型：`backend/src/main/java/com/wzh/suyuan/backend/model/ApiResponse.java`
- 实体类骨架：`backend/src/main/java/com/wzh/suyuan/backend/entity/`
  - `User`、`Product`、`Address`、`OrderEntity`、`OrderItem`
  - `TraceBatch`、`LogisticsNode`

## 4. 关键配置

- Android 网络请求基地址：`android/app/src/main/java/com/wzh/suyuan/network/NetworkConfig.java`
- 后端配置：`backend/src/main/resources/application.yml`
  - MySQL 连接信息与 JPA 基本参数
  - `globally_quoted_identifiers` 避免表名关键字冲突

## 5. 本阶段复用点

- MVP 基类结构、Fastjson Retrofit Converter、线程执行器等实现参考 `items/shop-mall-android` 的成熟实现方式并作简化与适配。

## 6. 运行建议

- Android：使用 Android Studio 打开 `android/`，同步 Gradle 后运行 `MainActivity`。
- 后端：在 `backend/` 执行 `mvn spring-boot:run`，确保本地 MySQL 已按 `application.yml` 配置完成连接。
