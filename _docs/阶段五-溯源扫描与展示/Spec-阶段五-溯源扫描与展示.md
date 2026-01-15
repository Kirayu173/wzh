# Spec: `S5-TRACE-SCAN` 溯源扫描与展示

## 0. 结论摘要（≤8行）

- 本卡交付的最小闭环：创建溯源批次 → 生成二维码/溯源码 → App 扫码或手动输入 → 查询溯源详情 → 展示物流节点时间线 → 保存扫码记录。
- 不做的事（Out-of-scope）：第三方物流接口对接、溯源评价体系、管理端 UI（阶段六）。
- 关键待决策点（最多3条）：
  - 二维码内容：推荐 traceCode（短码）；可选 traceUrl（完整 URL）。影响扫码解析逻辑与二维码长度/容错率。
  - 溯源详情接口形态：推荐单接口聚合（批次+物流节点一次返回）；可选分接口（批次与物流分开）。影响客户端请求次数与缓存策略。
  - 扫码记录存储：推荐本地 Room 记录；可选服务端记录（需新增接口与隐私告知）。影响多端一致性与数据合规性。

## 1. Scope

### 1.1 Must

- 后端提供溯源批次创建接口（管理端权限），返回 traceCode。
- 后端提供溯源详情查询接口（面向用户，支持 traceCode 查询）。
- 后端提供物流节点新增接口（管理端权限）。
- 后端提供二维码生成接口（返回图片 URL 或二进制流）。
- Android 集成扫码能力（ZXing），识别二维码内容并发起查询。
- 溯源详情页展示：批次信息 + 物流节点时间线（按时间排序）。
- 扫码记录保存与列表展示（至少本地记录最近扫码条目）。
- 失败/降级：扫码失败提示并支持手动输入；网络失败提示并允许重试；无物流节点时展示空态。
- 回滚：创建溯源批次/新增物流节点失败时不产生部分数据；扫码记录保存失败不影响溯源详情展示。

### 1.2 Should（推荐）

- 复用阶段三/四网络层与统一响应结构，减少重复解析代码。
- 采用 traceCode 作为二维码内容，二维码更短、识别率更高。
- 物流节点按 `node_time` 降序展示，时间线视觉更清晰。
- 使用 `items/zxing-android-embedded` 作为扫码方案（Android），降低集成成本。
- 扫码记录包含时间戳与 traceCode，便于检索与追溯。

### 1.3 Could（可选增强）

- 溯源详情支持报告图片预览/放大。
- 扫码记录支持搜索与清除历史。
- 溯源详情支持本地缓存最近一次数据（离线兜底）。

### 1.4 Out-of-scope（明确不做）

- 第三方物流轨迹实时查询。
- 溯源评价/评论功能（参考项目中存在，但本阶段不做）。
- 管理端溯源录入 UI（阶段六实现）。

## 2. 用户故事 & 验收标准（AC）

- AC-001: Given 管理员已登录 When 提交溯源批次信息 Then 返回 traceCode 与批次 id。
- AC-002: Given 用户输入有效 traceCode When 调用查询接口 Then 返回批次信息与物流节点列表。
- AC-003: Given 用户扫码获得 traceCode When 进入溯源详情页 Then 展示批次信息与物流时间线。
- AC-004: Given 扫码失败或二维码内容非法 When 用户提交解析 Then 提示错误并支持手动输入。
- AC-005: Given 物流节点为空 When 查看溯源详情 Then 展示“暂无物流信息”空态。
- AC-006: Given 管理员新增物流节点 When 提交成功 Then 查询接口返回包含新节点的列表。
- AC-007: Given 扫码成功 When 保存扫码记录 Then 扫码记录列表可查询到该记录。
- AC-008: Given 查询接口网络失败 When 用户重试 Then 可再次触发请求且不会错误更新本地记录。

## 3. 数据与契约（按需）

### 3.1 数据模型（本地/后端）

- trace_batch（后端）
  - id: bigint PK（推荐）
  - product_id: bigint（推荐）
  - trace_code: varchar(64) unique（推荐）
  - batch_no: varchar(64)（可选）
  - origin: varchar(128)（推荐）
  - producer: varchar(128)（推荐）
  - harvest_date: date（可选）
  - process_info: text（可选）
  - test_org/test_date/test_result/report_url: 质检相关字段（可选）
- logistics_node（后端）
  - id: bigint PK（推荐）
  - trace_code: varchar(64) not null（推荐）
  - node_time: datetime（推荐）
  - location: varchar(128)（推荐）
  - status_desc: varchar(255)（推荐）
- scan_record（本地）
  - id: bigint PK（推荐）
  - trace_code: varchar(64) not null（推荐）
  - scan_time: datetime（推荐）
  - product_name: varchar(128)（可选）

### 3.2 API 契约（如需要）

- POST /admin/trace（需鉴权，管理员）
  - Request: {"productId":1,"origin":"山东","producer":"合作社A","batchNo":"B001",...}
  - Response: {"code":0,"message":"OK","data":{"id":10,"traceCode":"TR20260101A0001"}}
- GET /trace/{traceCode}
  - Response: {"code":0,"message":"OK","data":{"batch":{...},"logistics":[...]}}
- POST /admin/trace/{traceCode}/logistics（需鉴权，管理员）
  - Request: {"nodeTime":"2026-02-12T10:00:00","location":"仓库A","statusDesc":"已出库"}
  - Response: {"code":0,"message":"OK","data":{"id":100}}
- GET /admin/trace/{traceCode}/qrcode（需鉴权，管理员）
  - Response: 二进制图片或 {"code":0,"data":{"url":"..."}}
- 错误码：400 参数错误、401 未认证、403 无权限、404 溯源不存在、409 traceCode 冲突【待查证】。

### 3.3 状态机（UI/业务）

- 扫码页：Idle → Scanning → Success | Error
- 溯源详情：Loading → Content | Empty | Error
- 扫码记录：Loading → Content | Empty | Error

## 4. 复用建议（一定要仔细阅读复用清单以及各阶段可复用清单目录下的各份文件后进行推荐）

- 可参考的 repo/模块/思路：
  - `items/suyuan-uniapp/pages/home/index.vue`：扫码入口与扫码逻辑参考。
  - `items/suyuan-uniapp/pages/ncpsy/index.vue`：溯源详情信息分块展示参考。
  - `items/suyuan-uniapp/pages/user/smjl.vue`：扫码记录列表样式与交互参考。
  - `items/zxing-android-embedded`：ZXing 扫码集成参考（Android）。
- 若计划复制代码：需要你后续确认 license/notice（此处只提示，不下结论）。

## 5. 测试计划（自动化）

- UT: 覆盖 AC-004、AC-005、AC-007（二维码解析、时间线排序、扫码记录保存）
- IT: 覆盖 AC-001、AC-002、AC-006、AC-008（溯源创建/查询/物流节点/异常处理）
- UI/E2E: 覆盖 AC-003、AC-004（扫码→详情展示→错误提示）

## 6. 可观测性（未上线也适用）

- 必打日志（字段、脱敏建议）
  - Android：扫码成功/失败、traceCode、请求结果（用户 id 脱敏）。
  - Backend：溯源创建/查询/物流新增耗时、traceCode、userId、结果码与异常。
- 指标建议（延迟/错误/流量/饱和度，按需）
  - 扫码成功率、溯源查询成功率、溯源查询 P95 延迟、4xx/5xx 比例。
- 追踪建议（可选）
  - requestId/traceId 贯通扫码查询链路。

## 7. 实施任务拆分（给 Codex 的 Task List）

- T-001 设计并实现溯源批次创建与查询接口（关联 AC-001、AC-002）
- T-002 设计并实现物流节点新增接口（关联 AC-006）
- T-003 设计并实现二维码生成接口（关联 AC-001）
- T-004 Android 集成 ZXing 扫码与解析逻辑（关联 AC-003、AC-004）
- T-005 Android 溯源详情页与物流时间线展示（关联 AC-003、AC-005）
- T-006 Android 扫码记录保存与列表页（关联 AC-007）
- T-007 联调与异常处理补齐（关联 AC-008）

## 8. Definition of Done（DoD）

- [ ] 所有 Must 的 AC 通过
- [ ] 自动化测试通过（UT/IT/UI 覆盖点已执行）
- [ ] 最小可观测性到位（扫码/溯源关键日志）
- [ ] 文档/变更记录更新（如需要）
