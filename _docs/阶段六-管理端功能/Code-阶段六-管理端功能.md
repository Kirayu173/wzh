# 阶段六代码文档（管理端功能）

## 1. 代码结构概览

- Android 客户端：`android/`
  - 管理端入口：`android/app/src/main/java/com/wzh/suyuan/feature/main/fragment/ProfileFragment.java`
  - 管理端首页：`android/app/src/main/java/com/wzh/suyuan/feature/admin/AdminHomeActivity.java`
  - 商品管理：`android/app/src/main/java/com/wzh/suyuan/feature/admin/product/`
  - 订单管理：`android/app/src/main/java/com/wzh/suyuan/feature/admin/order/`
  - 溯源管理：`android/app/src/main/java/com/wzh/suyuan/feature/admin/trace/`
  - 网络模型：`android/app/src/main/java/com/wzh/suyuan/network/model/`
  - 页面布局：`android/app/src/main/res/layout/activity_admin_*.xml`
- 后端服务：`backend/`
  - 管理端商品接口：`backend/src/main/java/com/wzh/suyuan/backend/controller/AdminProductController.java`
  - 管理端订单接口：`backend/src/main/java/com/wzh/suyuan/backend/controller/AdminOrderController.java`
  - 管理端溯源接口：`backend/src/main/java/com/wzh/suyuan/backend/controller/TraceAdminController.java`
  - 业务服务：`backend/src/main/java/com/wzh/suyuan/backend/service/ProductService.java`、`OrderService.java`、`TraceService.java`
  - DTO 与仓库：`backend/src/main/java/com/wzh/suyuan/backend/dto/`、`repository/`

## 2. Android 端实现说明

### 2.1 管理端隐藏入口
- 入口位置：个人中心版本号连续点击 5 次
- 角色校验：读取登录用户角色，非 admin 提示“无权限”

### 2.2 商品管理
- 列表页：`AdminProductListActivity`
  - 支持下拉刷新、空态/错误态
  - 单行操作：编辑、上/下架、修改库存、删除
- 编辑页：`AdminProductEditActivity`
  - 复用产品详情接口预填字段
  - 表单校验：名称/价格/库存必填

### 2.3 订单管理
- 列表页：`AdminOrderListActivity`
  - 支持状态筛选（全部/待发货/已发货/已完成）
  - 订单操作：查看详情、发货
- 详情页：`AdminOrderDetailActivity`
  - 展示收货信息、订单时间、快递信息
  - PAID 状态显示发货按钮

### 2.4 溯源管理
- 列表页：`AdminTraceListActivity`
  - 批次列表、编辑、删除
  - 二维码预览、添加物流节点
- 编辑页：`AdminTraceEditActivity`
  - 全量更新策略（PUT），要求关键字段必填
- 物流节点：`AdminTraceLogisticsActivity`
  - 输入节点时间/地点/状态描述
- 二维码页：`AdminTraceQrCodeActivity`
  - 展示后端返回的 PNG 二进制流

## 3. 后端实现说明

### 3.1 商品管理接口
- `GET /admin/products`：分页列表，支持状态与关键字筛选
- `POST /admin/products`：创建商品
- `PUT /admin/products/{id}`：更新商品
- `PATCH /admin/products/{id}/status`：上/下架
- `PATCH /admin/products/{id}/stock`：更新库存
- `DELETE /admin/products/{id}`：删除商品

### 3.2 订单管理接口
- `GET /admin/orders`：按状态分页列表
- `GET /admin/orders/{id}`：订单详情
- `POST /admin/orders/{id}/ship`：发货（仅允许 PAID -> SHIPPED）

### 3.3 溯源管理接口
- `GET /admin/trace`：批次列表
- `POST /admin/trace`：创建批次
- `PUT /admin/trace/{id}`：批次全量更新
- `DELETE /admin/trace/{id}`：删除批次（级联删除物流节点）
- `POST /admin/trace/{traceCode}/logistics`：新增物流节点
- `GET /admin/trace/{traceCode}/qrcode`：获取二维码 PNG

## 4. 核心算法与逻辑

### 4.1 管理端入口触发
- 版本号在 800ms 内连续点击 5 次触发
- 触发后校验角色为 admin，否则提示无权限

### 4.2 订单发货状态流转
- 仅允许 `PAID -> SHIPPED`
- 发货接口写入快递信息与发货时间，事务保证一致性

### 4.3 溯源批次更新与删除
- 更新采用 PUT 全量写入，减少局部合并逻辑
- 删除批次前先删除对应物流节点，避免残留数据

## 5. 接口参数定义（核心）

- `POST /admin/products`
  - Request：`{"name":"苹果","price":12.5,"stock":100,"origin":"山东","coverUrl":"...","status":"online"}`
  - Response：`{"code":0,"data":{"id":1}}`
- `POST /admin/orders/{id}/ship`
  - Request：`{"expressNo":"SF123","expressCompany":"顺丰"}`
- `PUT /admin/trace/{id}`
  - Request：`{"productId":1,"origin":"山东","producer":"合作社A","batchNo":"B001",...}`
- `POST /admin/trace/{traceCode}/logistics`
  - Request：`{"nodeTime":"2026-02-12T10:00:00","location":"仓库A","statusDesc":"已出库"}`

## 6. 使用示例

```bash
# 新增商品
curl -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"name":"苹果","price":12.5,"stock":100,"origin":"山东","status":"online"}' \
  http://localhost:8080/admin/products

# 订单发货
curl -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"expressNo":"SF123","expressCompany":"顺丰"}' \
  http://localhost:8080/admin/orders/100/ship

# 更新溯源批次
curl -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"productId":1,"origin":"山东","producer":"合作社A"}' \
  http://localhost:8080/admin/trace/10
```

## 7. 注意事项

- 所有 `/admin/*` 接口均要求 admin 角色，后端强校验
- 二维码接口返回 PNG 二进制流，Android 端需按字节解码
- 批次更新为 PUT 全量更新，表单字段需完整提交

## 8. 常见问题

- 提示无权限？
  - 检查登录用户角色是否为 admin
- 发货失败？
  - 订单状态必须为 PAID
- 二维码加载失败？
  - 确认 traceCode 有效且管理端接口可访问

## 9. 测试说明

- 手工验证：
  - 隐藏入口 -> 管理端首页 -> 商品 CRUD
  - 订单列表 -> 订单详情 -> 发货
  - 溯源列表 -> 编辑批次/新增物流 -> 二维码展示