# Spec: `S3-PRODUCT-CART` 商品浏览与购物车

## 0. 结论摘要（≤8行）

- 本卡交付的最小闭环：基于阶段二登录态，首页商品列表 → 商品详情 → 加入购物车 → 购物车列表/数量调整/持久化。
- 不做的事（Out-of-scope）：商品搜索、分类筛选、商品收藏、下单/支付流程。
- 关键待决策点（最多3条）：
  - 购物车持久化：本地 Room + 可选后端同步 vs 仅后端存储；影响离线可用性、冲突处理与接口复杂度。
  - 图片轮播组件：复用 `items/shop-mall-android` 的 BannerViewPager（依赖 UltraViewPager【待查证】）vs Android ViewPager2；影响依赖引入与维护成本。
  - 价格字段类型：decimal 字符串 vs 以分为单位的整数；影响精度、计算与接口契约一致性。

## 1. Scope

### 1.1 Must

- 后端提供商品列表与商品详情的最小闭环接口（推荐 `/products`、`/products/{id}`），支持分页与排序。
- 后端提供购物车增删改查接口（推荐 `/cart` 相关），数据与用户绑定，需鉴权（沿用阶段二 JWT）。
- Android 在现有 `MainActivity` 底部导航中，实现首页商品列表与购物车页内容（替换占位）。
- 商品列表支持下拉刷新/分页加载，商品详情展示价格、库存、产地、描述与图片轮播。
- 购物车支持添加、数量调整、选中/取消选中、删除与总价计算。
- 购物车数据持久化（本地或后端，需明确策略），App 重启后可恢复。
- 失败/降级：网络或接口失败时提示错误并可重试；若有本地缓存则优先展示缓存数据。
- 回滚：购物车数量/选中状态更新失败时回退到上一次有效状态并提示原因。

### 1.2 Should（推荐）

- 推荐复用现有网络层 `ApiClient/ApiService` 与统一响应 `BaseResponse/ApiResponse` 结构。
- 推荐商品图片加载使用缓存策略（如 Glide【待查证】或现有工具类），降低滚动卡顿。
- 推荐购物车本地缓存使用 Room（若后端已实现同步，可选择双写或单写策略）。
- 推荐详情页底部操作栏与数量选择复用 `items/shop-mall-android` 组件形态（NumberButton/BannerViewPager）。
- 推荐列表与详情页面提供空态与错误态占位，便于测试回归。

### 1.3 Could（可选增强）

- 商品列表支持骨架屏或占位加载动画。
- 商品详情展示“推荐商品/猜你喜欢”（可复用列表样式）。
- 购物车支持“全选/反选”与批量删除优化交互。

### 1.4 Out-of-scope（明确不做）

- 商品搜索、分类筛选、收藏/点赞。
- 订单创建、支付、地址管理。
- 管理端商品编辑与上下架。

## 2. 用户故事 & 验收标准（AC）

- AC-001: Given 用户已登录 When 打开首页商品列表 Then 成功加载分页列表并支持刷新与加载更多。
- AC-002: Given 商品列表已展示 When 点击某商品 Then 跳转详情并展示名称、价格、库存、产地与描述。
- AC-003: Given 商品详情包含多张图片 When 进入详情页 Then 图片轮播正常且指示器随滑动更新。
- AC-004: Given 用户在商品详情点击“加入购物车” When 请求成功或本地写入成功 Then 购物车数量/徽标更新。
- AC-005: Given 购物车存在商品 When 进入购物车页面 Then 展示购物车列表、选中状态与总价。
- AC-006: Given 用户调整购物车商品数量 When 修改成功 Then 列表与总价同步更新；失败则回滚数量并提示。
- AC-007: Given 用户删除或取消选中购物车商品 When 操作完成 Then 列表与总价按最新状态刷新。
- AC-008: Given 网络异常或接口失败 When 获取列表/详情/购物车 Then 显示错误提示并提供重试入口（若有缓存则展示缓存）。

## 3. 数据与契约（按需）

### 3.1 数据模型（本地/后端）

- product（后端，推荐）
  - id: bigint PK（推荐）
  - name: varchar(64) not null（推荐）
  - price: decimal(10,2) 或 bigint cents（二选一，待决策）
  - stock: int not null default 0（推荐）
  - cover_url: varchar(255) not null（推荐）
  - desc: text（可选）
  - origin: varchar(64)（可选）
  - status: tinyint default 1（可选）
- product_image（后端，可选）
  - id: bigint PK（可选）
  - product_id: bigint FK（推荐）
  - url: varchar(255) not null（推荐）
  - sort: int default 0（可选）
- cart_item（后端/本地）
  - id: bigint PK（推荐）
  - user_id: bigint not null（推荐）
  - product_id: bigint not null（推荐）
  - quantity: int not null default 1（推荐）
  - selected: boolean default true（推荐）
  - price_snapshot: decimal 或 bigint（可选，用于价格快照）
  - image/name: varchar（可选，减少列表再查询）

### 3.2 API 契约（如需要）

- GET /products（推荐）
  - Query: page, size, sort（可选）
  - Response: {"code":0,"message":"OK","data":{"items":[...],"page":1,"size":10,"total":100}}
- GET /products/{id}（推荐）
  - Response: {"code":0,"message":"OK","data":{"id":1,"name":"...","images":[{"url":"..."}]}}
- GET /cart（推荐，需鉴权）
  - Header: Authorization: Bearer <token>
  - Response: {"code":0,"message":"OK","data":[{"id":1,"productId":2,"quantity":1,"selected":true}]}
- POST /cart（推荐，需鉴权）
  - Request: {"productId":2,"quantity":1}
  - Response: {"code":0,"message":"OK","data":{"id":1}}
- PUT /cart/{id}（推荐，需鉴权）
  - Request: {"quantity":2}
- PATCH /cart/{id}/select（推荐，需鉴权）
  - Request: {"selected":true}
- DELETE /cart/{id}（推荐，需鉴权）
- 错误码：400 参数错误、401 未认证、404 商品不存在、409/422 库存不足【待查证】

### 3.3 状态机（UI/业务）

- 商品列表：Loading → Content | Empty | Error
- 商品详情：Loading → Content | Error
- 购物车：Loading → Content | Empty | Error | Partial（仅本地缓存）

## 4. 复用建议（一定要仔细阅读复用清单以及各阶段可复用清单目录下的各份文件后进行推荐）

- 可参考的 repo/模块/思路：
  - `items/shop-mall-android`：`fragment_goods_list.xml` + `item_goods.xml` 的列表布局；`fragment_goods_detail.xml` 与 `BannerViewPager` 的轮播结构；`fragment_cart.xml` + `item_cart.xml` 的购物车布局与 `NumberButton` 组件；`room/entity/Cart.java` 与 `room/dao/CartDao.java` 的本地持久化建模。
  - `items/suyuan-uniapp`：`uni-goods-nav` 组件（底部“加入购物车/立即购买”布局与交互参考）。
- 若计划复制代码：需要你后续确认 license/notice（此处只提示，不下结论）

## 5. 测试计划（自动化）

- UT: 覆盖 AC-004、AC-006、AC-007（数量变更逻辑、总价计算、持久化读写）
- IT: 覆盖 AC-001、AC-002、AC-004、AC-005（商品列表/详情/购物车接口链路）
- UI/E2E: 覆盖 AC-001、AC-003、AC-004、AC-005（列表→详情→加入购物车→购物车展示关键链路）

## 6. 可观测性（未上线也适用）

- 必打日志（字段、脱敏建议）
  - Android：商品列表/详情/购物车接口结果、分页参数、失败原因；用户 id 脱敏；图片 URL 仅记录域名。
  - Backend：接口耗时、请求 userId、结果码、异常堆栈（不记录敏感字段）。
- 指标建议（延迟/错误/流量/饱和度，按需）
  - 商品列表/详情 P95 延迟、购物车更新成功率、5xx/4xx 比例。
- 追踪建议（可选）
  - 统一 requestId 或 traceId 贯通商品与购物车接口。

## 7. 实施任务拆分（给 Codex 的 Task List）

- T-001 设计并实现商品列表/详情接口（关联 AC-001、AC-002、AC-003）
- T-002 设计并实现购物车接口（关联 AC-004、AC-005、AC-006、AC-007）
- T-003 实现 Android 商品列表页与分页刷新（关联 AC-001）
- T-004 实现 Android 商品详情页与轮播/加入购物车（关联 AC-002、AC-003、AC-004）
- T-005 实现 Android 购物车页与数量调整/删除/总价（关联 AC-005、AC-006、AC-007）
- T-006 接入购物车持久化与缓存策略（关联 AC-004、AC-005、AC-008）
- T-007 补充自动化测试与联调（关联 AC-001 至 AC-008）

## 8. Definition of Done（DoD）

- [ ] 所有 Must 的 AC 通过
- [ ] 自动化测试通过（UT/IT/UI 覆盖点已执行）
- [ ] 最小可观测性到位（商品/购物车关键日志）
- [ ] 文档/变更记录更新（如需要）
