# 阶段四代码文档（订单与支付功能）

## 1. 代码结构概览

- Android 客户端：`android/`
  - 地址管理：`android/app/src/main/java/com/wzh/suyuan/feature/address/`
  - 订单确认/支付：`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderConfirmActivity.java`、`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderPayActivity.java`
  - 订单列表/详情：`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderListActivity.java`、`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderDetailActivity.java`
  - 网络模型与接口：`android/app/src/main/java/com/wzh/suyuan/network/`
  - UI 布局资源：`android/app/src/main/res/layout/`
- 后端服务：`backend/`
  - 地址与订单接口：`backend/src/main/java/com/wzh/suyuan/backend/controller/`
  - 业务服务层：`backend/src/main/java/com/wzh/suyuan/backend/service/`
  - 数据实体与仓库：`backend/src/main/java/com/wzh/suyuan/backend/entity/`、`backend/src/main/java/com/wzh/suyuan/backend/repository/`

## 2. Android 端实现说明

### 2.1 地址管理

- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/address/AddressListActivity.java`
  - RecyclerView 展示地址列表，支持设为默认/编辑/删除
  - 空态与错误态统一用状态容器提示，支持重试
- 编辑页：`android/app/src/main/java/com/wzh/suyuan/feature/address/AddressEditActivity.java`
  - 新增与编辑复用同一页面
  - 必填字段校验（收货人/手机号/详细地址）

### 2.2 订单确认

- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderConfirmActivity.java`
  - 载入默认地址与购物车选中商品
  - 计算总价、支持备注输入、提交下单
  - 无默认地址时提示并提供管理入口

### 2.3 支付（模拟）

- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderPayActivity.java`
  - 展示订单号与金额
  - “模拟支付”调用 `/orders/{id}/pay` 更新状态

### 2.4 订单列表与详情

- 列表页：`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderListActivity.java`
  - 按状态 Tab 过滤（待付款/待发货/待收货/已完成）
  - 支持空态/错误态提示与重试
- 详情页：`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderDetailActivity.java`
  - 展示订单状态、地址、时间、商品明细与操作按钮
  - 支持支付/取消/确认收货操作

## 3. 后端实现说明

### 3.1 地址接口

- 控制器：`backend/src/main/java/com/wzh/suyuan/backend/controller/AddressController.java`
  - `GET /addresses`、`POST /addresses`、`PUT /addresses/{id}`、`DELETE /addresses/{id}`
  - `PATCH /addresses/{id}/default` 维护默认地址唯一性
- 服务：`backend/src/main/java/com/wzh/suyuan/backend/service/AddressService.java`
  - 新增地址自动处理默认值互斥逻辑

### 3.2 订单接口

- 控制器：`backend/src/main/java/com/wzh/suyuan/backend/controller/OrderController.java`
  - `POST /orders` 创建订单（基于购物车选中项）
  - `GET /orders` 列表查询（支持 status/page/size）
  - `GET /orders/{id}` 订单详情
  - `POST /orders/{id}/pay`、`/cancel`、`/confirm` 状态操作
- 服务：`backend/src/main/java/com/wzh/suyuan/backend/service/OrderService.java`
  - 下单时校验库存与商品状态，扣减库存
  - 取消订单回滚库存
  - 模拟支付更新 pay_time

### 3.3 数据模型

- 地址：`backend/src/main/java/com/wzh/suyuan/backend/entity/Address.java`
- 订单：`backend/src/main/java/com/wzh/suyuan/backend/entity/OrderEntity.java`（地址快照、备注、状态）
- 订单明细：`backend/src/main/java/com/wzh/suyuan/backend/entity/OrderItem.java`（价格/名称/图片快照）

## 4. 核心算法与逻辑

### 4.1 下单金额与库存计算

- 以购物车选中项为源，逐项校验库存与商品状态
- 使用当前商品价格作为订单价格快照，累加计算总金额
- 订单创建成功后扣减库存并清除对应购物车项
- 支持 `requestId` 幂等控制，重复请求返回同一订单

### 4.2 订单状态流转

- `PENDING_PAY → PAID → SHIPPED → COMPLETED`，并支持 `CANCELED`
- 取消订单仅允许 `PENDING_PAY`
- 确认收货在 `SHIPPED`（阶段六发货）或 `PAID`（阶段四演示）状态可用

### 4.3 默认地址互斥

- 设置默认地址时，自动将用户其他地址置为非默认
- 无默认地址时，订单确认页引导新增

## 5. 接口参数定义（核心）

- `POST /orders`
  - Request：`{"addressId":1,"items":[{"cartId":2,"productId":3,"quantity":2}],"memo":"...","requestId":"uuid"}`（`requestId` 用于幂等控制）
  - Response：`{"code":0,"message":"OK","data":{"id":10,"status":"PENDING_PAY","totalAmount":"199.00"}}`
- `GET /orders?status=PENDING_PAY&page=1&size=10`
  - Response：订单分页列表（含商品明细）
- `POST /orders/{id}/pay`
  - Response：订单详情（状态更新为 `PAID`）
- `POST /orders/{id}/cancel` / `POST /orders/{id}/confirm`
  - Response：订单详情（状态更新为 `CANCELED` / `COMPLETED`）
- `POST /addresses`
  - Request：`{"receiver":"张三","phone":"13800000000","province":"北京","city":"北京","detail":"朝阳路","isDefault":true}`

## 6. 使用示例

```bash
# 创建地址
curl -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"receiver":"张三","phone":"13800000000","province":"北京","city":"北京","detail":"朝阳路","isDefault":true}' \
  http://localhost:8080/addresses

# 创建订单
curl -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"addressId":1,"items":[{"cartId":2,"productId":3,"quantity":2}],"memo":"尽快送达"}' \
  http://localhost:8080/orders
```

## 7. 注意事项

- 阶段四为“模拟支付”，不接入第三方支付 SDK。
- 订单保存地址快照，避免后续地址修改影响历史订单展示。
- 取消订单会回滚库存，确保库存一致性。

## 8. 常见问题

- 订单确认页提示“暂无默认地址”：
  - 进入“地址管理”新增地址或设置默认地址。
- 订单确认页提示“请选择商品后再提交订单”：
  - 在购物车勾选商品后再次进入。
- 订单详情页缺少“确认收货”按钮：
  - 订单状态未达到 `PAID/SHIPPED`，可先完成支付或等待发货。

## 9. 测试说明

- 后端接口测试：`backend/src/test/java/com/wzh/suyuan/backend/OrderAddressControllerTest.java`
  - 覆盖地址默认逻辑、下单、支付、取消与确认收货流程

## 10. 运行建议

- Android：使用 Android Studio 打开 `android/`，登录后进入“购物车/个人中心”使用订单与地址功能。
- 后端：在 `backend/` 执行 `mvn spring-boot:run`，确保数据库可用并配置好 `application.yml`。
