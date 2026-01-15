# Spec: `S1-INIT` 项目初始化与架构搭建

## 0. 结论摘要（≤8行）

- 本卡交付的最小闭环：Android/后端工程可编译可运行，基础架构（MVP/MVVM、网络层、工具类、数据库框架）接入完成，并能进行启动验证。
- 不做的事（Out-of-scope）：任何业务功能与具体页面实现、后端业务API实现。
- 关键待决策点（最多3条）：
  - 架构选型：MVP（与 shop-mall-android 复用）或 MVVM（设计方案推荐）；影响复用成本与后续模块结构一致性。
  - JSON 解析与网络栈：Fastjson（复用）或 Gson（设计方案）；影响模型定义与序列化兼容性。
  - ZXing 依赖与 minSdk 21 兼容方案（降级 core 或 desugaring）；影响编译通过与运行稳定性。

## 1. Scope

### 1.1 Must

- Android 项目初始化与构建配置（minSdk 21、targetSdk 30、依赖管理）。
- 后端 Spring Boot 项目初始化与构建配置（Web/JPA/MySQL/Security/Lombok）。
- 架构基础层接入（推荐复用 MVP 基类或备选 MVVM 基础结构）。
- 网络层基础组件接入与配置（超时、拦截器、响应模型）。
- 基础工具类集成（SP、网络状态、格式化、Toast、状态栏等）。
- 数据库框架接入（Room + 基础 DAO + 迁移策略占位）。
- 编译/运行验证与最小日志记录。

### 1.2 Should（推荐）

- 推荐复用 shop-mall-android 的 MVP 基类与网络层组件（复用成本低，适配快）。
- 推荐建立后端数据库表结构初版与实体类骨架（不实现业务逻辑）。
- 推荐提供最小化启动验证点（Android 启动日志 + 后端启动日志）。
- 失败/降级（fallback）：若复用组件不兼容或依赖冲突，降级为“自建最小基类/最小网络封装/最小工具类”，保证可编译与可运行。

### 1.3 Could（可选增强）

- 增加基本的 /health 或 /ping 验证端点（若决定引入）。
- 提前引入 Mock 或本地假数据源，便于后续阶段接入。

### 1.4 Out-of-scope（明确不做）

- 用户注册登录、商品、订单、溯源等业务功能。
- UI 页面开发与交互。
- 业务 API 的完整实现与联调。

## 2. 用户故事 & 验收标准（AC）

- AC-001: Given Android 工程完成配置 When 执行 debug 构建 Then 编译通过且应用可在 API 21 设备启动无崩溃。
- AC-002: Given 后端工程完成配置 When 启动 Spring Boot Then 应用启动成功且日志显示数据库连接成功【待查证】。
- AC-003: Given 选定架构基类（MVP 或 MVVM） When 编译运行 Then 基类可被示例模块引用且无编译错误。
- AC-004: Given 网络层基础组件已接入 When 触发一次示例网络调用（可用 Mock） Then 请求能发出且响应可解析为 BaseResponse/对应模型。
- AC-005: Given Room 已接入 When 执行数据库初始化 Then 可创建数据库并完成至少一个 DAO 的空查询。
- AC-006: Given 工具类集成完成 When 在示例模块调用 Then SP/Toast/网络状态获取可用且无异常。

## 3. 数据与契约（按需）

### 3.1 数据模型（本地/后端）

以下为“推荐”字段清单（类型/约束需与实际数据库选型一致，未确认处标注【待查证】）：

- user（后端）
  - id: bigint PK auto_increment（推荐）【待查证】
  - username: varchar(64) unique not null（推荐）【待查证】
  - password_hash: varchar(255) not null（推荐）【待查证】
  - phone: varchar(20) nullable（推荐）【待查证】
  - role: varchar(32) default 'user'（推荐）【待查证】
  - create_time: datetime default now()（可选）【待查证】
- product（后端）
  - id: bigint PK auto_increment（推荐）【待查证】
  - name: varchar(128) not null（推荐）【待查证】
  - price: decimal(10,2) not null（推荐）【待查证】
  - stock: int not null default 0（推荐）【待查证】
  - cover_url: varchar(255) nullable（可选）【待查证】
  - origin/status/create_time: 见设计方案（推荐）【待查证】
- order / order_item / address / trace_batch / logistics_node
  - 字段列表参考 `设计方案.md` 的表清单；类型与约束在阶段一确定（推荐）【待查证】。

### 3.2 API 契约（如需要）

阶段一产出为“接口设计文档初版”，不要求实现：

- 认证：`POST /auth/register`、`POST /auth/login`（推荐）
- 商品：`GET /products`、`GET /products/{id}`（推荐）
- 订单：`POST /orders`、`POST /orders/{id}/pay`、`GET /orders`（推荐）
- 溯源：`POST /admin/trace`、`GET /trace/{traceCode}`（推荐）

### 3.3 状态机（UI/业务）

- Android 启动验证：Loading → Ready | Error
- 后端启动验证：Booting → Ready | Error

## 4. 复用建议（一定要仔细阅读复用清单以及各阶段可复用清单目录下的各份文件后进行推荐）

- 可参考的 repo/模块/思路：
  - `items/shop-mall-android`：MVP 基类、网络层组件、工具类库（推荐优先复用）。
  - `items/zxing-android-embedded`：ZXing 集成方式与 minSdk 兼容方案（阶段一仅配置依赖，后续阶段使用）。
  - `items/suyuan-uniapp`：主要用于业务与页面参考，本阶段仅作为设计参考（可选）。
- 若计划复制代码：需要你后续确认 license/notice（此处只提示，不下结论）。

## 5. 测试计划（自动化）

- UT: 覆盖 AC-003、AC-006（基类与工具类可被引用且方法可调用）。
- IT: 覆盖 AC-002、AC-005（后端启动与数据库初始化、Room 数据库初始化）。
- UI/E2E: 覆盖 AC-001（应用启动与基础页面加载的冒烟验证）。

## 6. 可观测性（未上线也适用）

- 必打日志（字段、脱敏建议）
  - Android 启动日志：appVersion、buildType、deviceApi；不记录用户敏感信息。
  - 后端启动日志：appVersion、profile、dbHost（脱敏）【待查证】。
- 指标建议（延迟/错误/流量/饱和度，按需）
  - 启动耗时（Android）、应用启动失败率（Android/后端）。
- 追踪建议（可选）
  - 初始化阶段可不接入分布式追踪。

## 7. 实施任务拆分（给 Codex 的 Task List）

- T-001 创建 Android 工程与 Gradle 配置（关联 AC-001）
- T-002 创建 Spring Boot 工程与基础配置（关联 AC-002）
- T-003 复用/建立架构基类（MVP/MVVM 选型后执行）（关联 AC-003）
- T-004 接入网络层组件与基础配置（关联 AC-004）
- T-005 集成工具类库并验证可用（关联 AC-006）
- T-006 接入 Room 与最小 DAO（关联 AC-005）
- T-007 启动验证与基础日志打点（关联 AC-001、AC-002）
- T-008 失败/降级与回滚方案验证（关联 AC-001、AC-002）

## 8. Definition of Done（DoD）

- [ ] 所有 Must 的 AC 通过
- [ ] 自动化测试通过（UT/IT/冒烟）
- [ ] 最小可观测性到位（启动日志与基础指标）
- [ ] 文档/变更记录更新（如需要）
