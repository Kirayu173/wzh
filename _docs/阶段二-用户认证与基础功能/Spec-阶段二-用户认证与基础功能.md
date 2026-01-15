# Spec: `S2-AUTH` 用户认证与基础功能

## 0. 结论摘要（≤8行）

- 本卡交付的最小闭环：用户注册/登录获取 Token，Android 保存登录态并进入基础主页面（首页/购物车/个人中心占位），个人中心可展示用户信息与退出登录。
- 不做的事（Out-of-scope）：商品/订单/溯源业务流程、第三方登录、短信验证码、管理端功能。
- 关键待决策点（最多3条）：
  - Token 方案（JWT/自定义令牌/Session），影响后端安全配置、客户端拦截器与过期处理。
  - 密码加密算法与强度（推荐 BCrypt，参数与成本【待查证】），影响安全性与登录性能。
  - 登录态存储位置（SP/Room/加密存储），影响安全性与离线可用性。

## 1. Scope

### 1.1 Must

- 后端提供注册、登录、Token 校验的最小闭环接口（推荐 `/auth/register`、`/auth/login`、`/auth/me`）。
- Android 提供登录/注册页面、表单校验、请求与结果处理。
- Android 保存 Token 与基本用户信息（推荐 SP），并在请求中携带 Token。
- 基础主页面框架（底部导航：首页/购物车/个人中心占位），可正常切换。
- 个人中心展示用户信息与版本信息；支持退出登录。
- 失败/降级：接口失败时提示错误并保持当前页面状态；无网络时提示并可重试。
- 回滚：Token 校验失败或手动退出后清除本地登录态并回到登录页。

### 1.2 Should（推荐）

- 后端密码加密（推荐 BCrypt），不返回敏感字段。
- 登录成功后自动拉取用户信息并缓存（推荐 SP 或本地轻量缓存）。
- Android 登录页可参考“注册/登录同页切换”交互（参考 `items/suyuan-uniapp`）。
- 统一响应模型与错误码规范（推荐使用现有 `BaseResponse/ApiResponse` 结构）。

### 1.3 Could（可选增强）

- Token 自动刷新或续期（需明确策略）。
- 角色字段驱动个人中心菜单差异（参考 `items/suyuan-uniapp` 的 role 逻辑）。
- Mock 登录模式用于无后端联调时的 UI 验证。

### 1.4 Out-of-scope（明确不做）

- 短信验证码、邮箱验证、第三方 OAuth。
- 用户资料编辑、头像上传、找回密码。
- 商品、订单、溯源等业务功能实现。

## 2. 用户故事 & 验收标准（AC）

- AC-001: Given 新用户提交注册信息 When 调用注册接口 Then 返回成功响应并生成用户记录。
- AC-002: Given 已注册用户提交正确账号密码 When 调用登录接口 Then 返回 Token 和用户基础信息。
- AC-003: Given 客户端已保存 Token When 打开应用并访问主页面 Then 自动进入主页面且请求携带 Token。
- AC-004: Given Token 无效或过期 When 访问需要认证的接口 Then 返回 401 并触发客户端清理登录态与跳转登录页。
- AC-005: Given 用户已登录 When 进入个人中心 Then 正确展示用户信息与版本信息。
- AC-006: Given 用户点击退出登录 When 确认退出 Then 清除本地 Token 并返回登录页。
- AC-007: Given 应用主页面已进入 When 点击底部导航 Then 对应页面正确切换。

## 3. 数据与契约（按需）

### 3.1 数据模型（本地/后端）

- user（后端，推荐）
  - id: bigint PK auto_increment（推荐）
  - username: varchar(64) unique not null（推荐）
  - password_hash: varchar(255) not null（推荐）
  - phone: varchar(20) nullable（可选）
  - role: varchar(32) default 'user'（推荐）
  - create_time: datetime default now()（可选）
- token（本地，Android，推荐）
  - token: string（推荐，存 SP）
  - expire_at: long（可选）
  - user_id/username/role: string（推荐缓存基础信息）

### 3.2 API 契约（如需要）

- POST /auth/register（推荐）
  - Request: {"username": "u", "password": "p", "phone": ""}
  - Response: {"code": 0, "message": "OK", "data": {"id": 1, "username": "u"}}
- POST /auth/login（推荐）
  - Request: {"username": "u", "password": "p"}
  - Response: {"code": 0, "message": "OK", "data": {"token": "...", "expireAt": 0, "user": {"id": 1, "username": "u", "role": "user"}}}
- GET /auth/me（推荐）
  - Header: Authorization: Bearer <token>（推荐）
  - Response: {"code": 0, "message": "OK", "data": {"id": 1, "username": "u", "role": "user"}}
- 错误码：400 参数错误、401 未认证、409 用户已存在（推荐，具体需【待查证】）

### 3.3 状态机（UI/业务）

- 登录页：Idle → Submitting → Success | Error
- 主页面：Loading → Content | Error
- 个人中心：Loading → Content | Empty | Error

## 4. 复用建议（一定要仔细阅读复用清单以及各阶段可复用清单目录下的各份文件后进行推荐）

- 可参考的 repo/模块/思路：
  - `items/shop-mall-android`：`LoginFragment/LoginPresenter/LoginModel` 的 MVP 结构与请求组织方式；`Member` 模型与 `AppUtils.saveMember/exitLogin` 的 Token 存储/清理逻辑；`SPUtils` 的本地缓存方式。
  - `items/suyuan-uniapp`：`pages/login/index.vue` 的登录/注册同页切换交互；`pages/user/index.vue` 的角色区分与退出登录流程；`api/index.js` 的登录/注册接口组织方式（仅作交互参考）。
- 若计划复制代码：需要你后续确认 license/notice（此处只提示，不下结论）

## 5. 测试计划（自动化）

- UT: 覆盖 AC-002、AC-006（登录逻辑、Token 存取/清理、表单校验）
- IT: 覆盖 AC-001、AC-002、AC-004（注册/登录/Token 校验链路）
- UI/E2E: 覆盖 AC-003、AC-005、AC-007（登录进入主页面、个人中心展示、底部导航切换）

## 6. 可观测性（未上线也适用）

- 必打日志（字段、脱敏建议）
  - Android：login/register 结果、token 存取结果（token 仅记录前后 4 位），用户 id 脱敏。
  - Backend：auth 成功/失败、异常原因、requestId，敏感字段不入日志。
- 指标建议（延迟/错误/流量/饱和度，按需）
  - 登录/注册接口 P95 延迟、成功率、401 比例。
- 追踪建议（可选）
  - 接口链路打点（如引入 traceId）。

## 7. 实施任务拆分（给 Codex 的 Task List）

- T-001 设计并实现注册/登录/用户信息接口（关联 AC-001、AC-002、AC-004）
- T-002 实现 Android 登录/注册 UI 与表单校验（关联 AC-001、AC-002）
- T-003 接入 Token 存储与请求拦截（关联 AC-003、AC-004、AC-006）
- T-004 实现主页面框架与底部导航（关联 AC-007）
- T-005 实现个人中心展示与退出登录（关联 AC-005、AC-006）
- T-006 补充测试用例与自动化测试（关联 AC-001 至 AC-007）

## 8. Definition of Done（DoD）

- [ ] 所有 Must 的 AC 通过
- [ ] 自动化测试通过（UT/IT/UI 覆盖点已执行）
- [ ] 最小可观测性到位（登录/注册/Token 关键日志）
- [ ] 文档/变更记录更新（如需要）