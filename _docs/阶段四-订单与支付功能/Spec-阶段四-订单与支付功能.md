# Spec: `S4-ORDER-PAY` 订单与支付功能

## 0. 结论摘要（≤8行）

- 本卡交付的最小闭环：基于阶段三商品/购物车能力，完成地址管理 → 订单确认 → 下单 → 模拟支付 → 订单列表/详情/取消/确认收货。
- 不做的事（Out-of-scope）：真实支付接入、订单搜索/统计、管理端发货/改状态、发票/优惠券体系。
- 关键待决策点（最多3条）：
  - 支付实现：推荐“模拟支付”接口；可选接入支付宝/微信 SDK（密钥/回调配置【待查证】）；影响依赖体积与合规风险。
  - 订单地址存储：推荐保存地址快照（receiver/phone/address）或保存 address_id（待决策）；影响历史订单可追溯与地址变更后的展示一致性。
  - 下单来源：推荐从购物车“选中项”创建；可选“立即购买”直达下单；影响接口参数与库存扣减策略。

## 1. Scope

### 1.1 Must

- 地址管理：新增/编辑/删除/设置默认，需鉴权（沿用阶段二 JWT）。
- 订单确认页：展示地址、商品明细、总价、备注输入与提交下单。
- 订单创建：校验库存与商品状态，生成订单与订单明细，返回订单信息。
- 模拟支付：调用支付接口后更新订单状态与支付时间。
- 订单列表：支持按状态筛选（至少待付款/待发货/待收货/已完成），含空态与错误态。
- 订单详情：展示订单基础信息、商品列表、地址信息与可操作按钮（支付/取消/确认收货）。
- 订单操作：取消订单、确认收货，需校验状态合法性。
- 失败/降级（fallback）：接口失败时提示并允许重试；若列表本地缓存存在可展示最近一次数据（可选）。
- 回滚：下单失败不清空购物车；支付/取消/确认收货失败时保持原状态并提示原因。

### 1.2 Should（推荐）

- 推荐复用阶段三已有网络层与统一响应结构，减少重复解析代码。
- 推荐订单创建使用购物车选中项并在成功后移除（或标记）对应购物车记录。
- 推荐订单状态统一为枚举/常量映射（如 PENDING_PAY/PAID/SHIPPED/COMPLETED/CANCELED）。
- 推荐地址默认值保证唯一（更新默认时同用户其他地址置为 false）。
- 推荐订单确认页提供“无默认地址”引导新增入口。

### 1.3 Could（可选增强）

- 支付结果页（成功/失败）与订单状态卡片样式优化。
- 订单列表支持“全部”Tab 与列表下拉刷新。
- 订单详情提供复制订单号/物流信息展示（物流接口在后续阶段对接）。

### 1.4 Out-of-scope（明确不做）

- 第三方真实支付、退款、分账。
- 管理端发货/改状态（阶段六）。
- 订单搜索、评价、发票、优惠券。

## 2. 用户故事 & 验收标准（AC）

- AC-001: Given 用户已登录且进入地址管理 When 新增或编辑地址并保存 Then 地址列表更新且默认地址可被正确标记。
- AC-002: Given 购物车已选中商品 When 进入订单确认页 Then 展示商品明细、收货地址与总价并可提交订单。
- AC-003: Given 用户提交订单 When 库存与商品状态校验通过 Then 创建订单与明细并返回订单 id 与状态=待付款。
- AC-004: Given 订单处于待付款 When 点击“模拟支付” Then 订单状态变更为已支付且记录支付时间。
- AC-005: Given 用户进入订单列表 When 切换不同状态 Tab Then 仅展示对应状态订单并支持空态/错误态提示。
- AC-006: Given 订单为待付款 When 用户取消订单 Then 订单状态变更为已取消且列表/详情同步更新。
- AC-007: Given 订单为待收货 When 用户确认收货 Then 订单状态变更为已完成并记录确认时间（如有）。
- AC-008: Given 任意订单操作遇到非法状态或网络失败 When 操作提交 Then 提示错误且订单状态保持不变。

## 3. 数据与契约（按需）

### 3.1 数据模型（本地/后端）

- address（后端/本地）
  - id: bigint PK（推荐）
  - user_id: bigint not null（推荐）
  - receiver: varchar(64) not null（推荐）
  - phone: varchar(20) not null（推荐）
  - province/city: varchar(32)（可选）
  - detail: varchar(255)（推荐）
  - is_default: boolean default false（推荐）
  - create_time: datetime（可选）
- order（后端）
  - id: bigint PK（推荐）
  - user_id: bigint not null（推荐）
  - total_amount: decimal(10,2)（推荐）
  - status: varchar(32)（推荐，建议枚举常量）
  - pay_time/ship_time: datetime（可选）
  - express_no/express_company: varchar(64)（可选）
  - address_id 或 receiver/phone/address 快照（二选一，待决策）
  - create_time: datetime（推荐）
- order_item（后端）
  - id: bigint PK（推荐）
  - order_id: bigint not null（推荐）
  - product_id: bigint not null（推荐）
  - price: decimal(10,2) not null（推荐，价格快照）
  - quantity: int not null（推荐）

### 3.2 API 契约（如需要）

- GET /addresses（推荐，需鉴权）
- POST /addresses（推荐，需鉴权）
- PUT /addresses/{id}（推荐，需鉴权）
- DELETE /addresses/{id}（推荐，需鉴权）
- PATCH /addresses/{id}/default（推荐，需鉴权）
- POST /orders（推荐，需鉴权）
  - Request: {"addressId":1,"items":[{"cartId":2,"productId":3,"quantity":2}],"memo":"..."}
  - Response: {"code":0,"message":"OK","data":{"id":10,"status":"PENDING_PAY","totalAmount":"199.00"}}
- GET /orders（推荐，需鉴权，支持 status/page/size）
- GET /orders/{id}（推荐，需鉴权）
- POST /orders/{id}/pay（模拟支付，需鉴权）
- POST /orders/{id}/cancel（推荐，需鉴权）
- POST /orders/{id}/confirm（推荐，需鉴权）
- 错误码：400 参数错误、401 未认证、404 订单不存在、409 状态冲突/库存不足【待查证】

### 3.3 状态机（UI/业务）

- 订单状态：PENDING_PAY → PAID → SHIPPED → COMPLETED；PENDING_PAY → CANCELED（推荐）
- 订单确认页：Loading → Content | Empty | Error
- 订单列表：Loading → Content | Empty | Error | Partial（仅本地缓存）
- 订单详情：Loading → Content | Error

## 4. 复用建议（一定要仔细阅读复用清单以及各阶段可复用清单目录下的各份文件后进行推荐）

- 可参考的 repo/模块/思路：
  - `items/shop-mall-android`：`fragment/submit_order` 的提交流程与地址选择逻辑；`fragment/order` + `fragment/order_list` 的 Tab 列表结构；`fragment/select_pay` 的支付方式列表与按钮交互（第三方 SDK 部分为可选参考）；`bean/Order.java`、`bean/OrderItem.java`、`bean/Address.java` 的数据字段设计。
  - `items/suyuan-uniapp`：`uni-segmented-control` 的 Tab 切换样式与交互（仅做 UI 参考）。
- 若计划复制代码：需要你后续确认 license/notice（此处只提示，不下结论）

## 5. 测试计划（自动化）

- UT: 覆盖 AC-001、AC-003、AC-006、AC-007（地址默认逻辑、下单金额计算、订单状态流转）
- IT: 覆盖 AC-002、AC-003、AC-004、AC-005、AC-008（下单/支付/列表/详情/状态冲突接口）
- UI/E2E: 覆盖 AC-002、AC-004、AC-005（购物车选中→确认订单→支付→订单列表/详情）

## 6. 可观测性（未上线也适用）

- 必打日志（字段、脱敏建议）
  - Android：下单/支付/取消/确认收货的请求结果、订单 id、状态变化（用户 id 脱敏）。
  - Backend：订单创建/支付/取消/确认收货的耗时、userId、orderId、结果码与异常信息。
- 指标建议（延迟/错误/流量/饱和度，按需）
  - 下单成功率、支付成功率、订单列表 P95 延迟、4xx/5xx 比例。
- 追踪建议（可选）
  - requestId/traceId 贯通下单与支付链路。

## 7. 实施任务拆分（给 Codex 的 Task List）

- T-001 设计并实现地址管理接口与数据模型（关联 AC-001）
- T-002 设计并实现订单创建/支付/取消/确认接口（关联 AC-003、AC-004、AC-006、AC-007、AC-008）
- T-003 Android 实现地址列表/编辑页（关联 AC-001）
- T-004 Android 实现订单确认页与提交流程（关联 AC-002、AC-003）
- T-005 Android 实现支付页（模拟支付）与订单详情操作（关联 AC-004、AC-006、AC-007）
- T-006 Android 实现订单列表与状态筛选（关联 AC-005）
- T-007 联调与自动化测试补齐（关联 AC-001 至 AC-008）

## 8. Definition of Done（DoD）

- [ ] 所有 Must 的 AC 通过
- [ ] 自动化测试通过（UT/IT/UI 覆盖点已执行）
- [ ] 最小可观测性到位（订单/支付关键日志）
- [ ] 文档/变更记录更新（如需要）
