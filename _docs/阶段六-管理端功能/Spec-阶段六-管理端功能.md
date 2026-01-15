# Spec: `S6-ADMIN-MGMT` 管理端功能

## 0. 结论摘要（≤8行）

- 本卡交付的最小闭环：隐藏入口进入管理端 → 商品管理 → 订单发货 → 溯源批次管理/二维码展示
- 不做的事（Out-of-scope）：用户管理、数据统计、报表导出、Web 管理端
- 关键待决策点（最多3条）：
  - 商品图片：推荐录入 URL；可选本地上传（需对象存储/上传接口【待查证】），影响前后端接口与权限配置
  - 订单状态调整：推荐仅开放发货（PAID→SHIPPED）；可选管理员手动改状态，影响风控与数据一致性
  - 溯源批次更新策略：推荐全量更新（PUT）；可选局部更新（PATCH），影响前端表单与校验复杂度

## 1. Scope

### 1.1 Must

- 管理端隐藏入口（个人中心版本号连续点击 5 次）与角色校验
- 商品管理：新增、编辑、上下架、修改库存、删除
- 订单管理：列表/筛选、详情、发货（填写快递信息）
- 溯源管理：批次列表、编辑、删除、生成二维码、添加物流节点
- 权限控制：后端强校验 admin 角色，前端二次校验
- 失败/降级：请求失败提示并允许重试；空数据明确展示；权限不足提示并阻止进入
- 回滚：管理端写操作失败不更新列表；后端操作采用事务保证数据一致性

### 1.2 Should（推荐）

- 复用现有 MVP 基础架构与网络层封装，减少重复代码
- 管理端列表支持状态筛选与关键字段检索（名称/订单号/溯源码）
- 二维码页面支持保存到本地相册与系统分享（需权限适配）

### 1.3 Could（可选增强）

- 批量上下架/批量删除
- 管理端操作审计日志页（仅展示，不做统计）
- 溯源批次支持预览检测报告图片

### 1.4 Out-of-scope（明确不做）

- 用户管理
- 数据统计与报表导出
- 第三方物流对接
- Web 管理后台

## 2. 用户故事 & 验收标准（AC）

- AC-001: Given 已登录且角色为 admin When 在个人中心连续点击版本号 5 次 Then 进入管理端主页并展示三大模块入口
- AC-002: Given 角色非 admin When 触发管理端入口 Then 提示“无权限”并阻止进入
- AC-003: Given 管理员在商品管理页 When 新增/编辑商品并保存 Then 列表展示最新数据且前台可见状态与库存正确
- AC-004: Given 管理员在订单管理页 When 对 PAID 订单提交发货信息 Then 订单状态变为 SHIPPED 且记录快递信息与发货时间
- AC-005: Given 管理员在溯源管理页 When 编辑批次或新增物流节点 Then 溯源详情接口返回最新数据且物流时间线按时间倒序展示
- AC-006: Given 管理员请求二维码 When 获取二维码接口 Then 返回可展示的 PNG 图并可在管理端预览
- AC-007: Given 网络异常或服务端错误 When 管理端执行任一写操作 Then 前端不更新列表并提示可重试
- AC-008: Given 管理员删除商品或溯源批次 When 删除成功 Then 列表移除该条目且后端不残留孤立数据

## 3. 数据与契约（按需）

### 3.1 数据模型（本地/后端）

- Product（后端）
  - id: bigint PK
  - name: varchar(128) not null（推荐）
  - price: decimal(10,2) not null（推荐，>=0）
  - stock: int not null（推荐，>=0）
  - cover_url: varchar(255)（可选）
  - origin: varchar(64)（可选）
  - description: text（可选）
  - status: varchar(32) default "online"（推荐：online/offline）
  - create_time: datetime（推荐）
- Order（后端）
  - id/user_id/total_amount/status/pay_time/ship_time/confirm_time
  - express_no/express_company/receiver/phone/address/memo
- TraceBatch/LogisticsNode（后端）
  - 继承阶段五字段
  - 删除批次时推荐级联删除物流节点

### 3.2 API 契约（如需要）

- POST /admin/products
  - Request: {"name":"苹果","price":12.5,"stock":100,"origin":"山东","coverUrl":"...","status":"online"}
  - Response: {"code":0,"message":"OK","data":{"id":1}}
- PUT /admin/products/{id}
  - Request: {"name":"苹果","price":12.5,"stock":100,"origin":"山东","coverUrl":"...","status":"online"}
- PATCH /admin/products/{id}/status
  - Request: {"status":"offline"}
- PATCH /admin/products/{id}/stock
  - Request: {"stock":120}
- DELETE /admin/products/{id}

- GET /admin/orders?status=PAID&page=1&size=10
  - Response: {"code":0,"data":{"items":[...],"page":1,"size":10,"total":100}}
- GET /admin/orders/{id}
- POST /admin/orders/{id}/ship
  - Request: {"expressNo":"SF123","expressCompany":"顺丰"}
  - Response: {"code":0,"data":{"id":100,"status":"SHIPPED"}}
- POST /admin/orders/{id}/status（可选）
  - Request: {"status":"SHIPPED"}（需明确允许的状态流转）

- GET /admin/trace
  - Response: {"code":0,"data":{"items":[{"id":1,"traceCode":"TR...","productId":1,"productName":"苹果"}]}}
- PUT /admin/trace/{id}
  - Request: {"origin":"山东","producer":"合作社A",...}
- DELETE /admin/trace/{id}
- GET /admin/trace/{traceCode}/qrcode（已有）
- POST /admin/trace/{traceCode}/logistics（已有）

- 错误码：400 参数错误；401 未认证；403 无权限；404 不存在；409 状态冲突

### 3.3 状态机（UI/业务）

- 管理端主页：Loading → Content | Error
- 列表页：Loading → Content | Empty | Error
- 表单页：Editing → Saving → Success | Error（失败回到 Editing）
- 订单发货：Idle → Submitting → Success | Error

## 4. 复用建议（一定要仔细阅读复用清单以及各阶段可复用清单目录下的各份文件后进行推荐）

- 可参考的 repo/模块/思路：
  - `items/suyuan-uniapp/pages/admin/produce.vue`：商品 CRUD 表格 + 表单校验
  - `items/suyuan-uniapp/pages/admin/suyuan.vue`：溯源批次列表 + 二维码预览/下载思路
  - `items/suyuan-uniapp/pages/admin/farm.vue`：通用列表/弹窗表单交互结构
  - `items/suyuan-uniapp/pages/home/index.vue`：角色入口与管理端导航思路
  - `items/shop-mall-android`：MVP 基类与 RecyclerView 列表交互样式参考
- 若计划复制代码：需要你后续确认 license/notice（此处只提示，不下结论）

## 5. 测试计划（自动化）

- UT: 覆盖 AC-003/AC-004/AC-005（商品状态/库存更新、订单发货状态流转、溯源批次更新与物流排序）
- IT: 覆盖 AC-002/AC-003/AC-004/AC-006/AC-008（权限校验、CRUD 接口、二维码接口、删除级联）
- UI/E2E: 覆盖 AC-001/AC-004/AC-006（隐藏入口 → 管理端主页 → 发货 → 二维码展示）

## 6. 可观测性（未上线也适用）

- 必打日志（字段、脱敏建议）
  - 后端：requestId、adminId（脱敏）、productId/orderId/traceCode、结果码、耗时
  - Android：管理端入口触发次数、请求结果、错误码（不记录敏感信息）
- 指标建议（延迟/错误/流量/饱和度，按需）
  - 管理端核心接口 P95 延迟、4xx/5xx 比例、发货成功率
- 追踪建议（可选）
  - requestId 在管理端操作链路打通

## 7. 实施任务拆分（给 Codex 的 Task List）

- T-001 管理端入口与权限校验（关联 AC-001/AC-002）
- T-002 后端商品管理接口与 DTO（关联 AC-003/AC-008）
- T-003 后端订单管理接口（发货/列表/详情）（关联 AC-004）
- T-004 后端溯源管理接口（列表/更新/删除/二维码/物流）（关联 AC-005/AC-006/AC-008）
- T-005 Android 管理端商品/订单/溯源页面与表单校验（关联 AC-003/AC-004/AC-005/AC-006）
- T-006 失败/降级与回滚处理（关联 AC-007）

## 8. Definition of Done（DoD）

- [ ] 所有 Must 的 AC 通过
- [ ] 自动化测试通过（mvn test、Android Lint、管理端接口 IT）
- [ ] 最小可观测性到位（关键日志与错误码覆盖）
- [ ] 文档/变更记录更新（阶段六 Spec）
