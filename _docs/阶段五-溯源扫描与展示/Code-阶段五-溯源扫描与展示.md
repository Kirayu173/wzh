# 阶段五代码文档（溯源扫描与展示）

## 1. 代码结构概览

- Android 客户端：`android/`
  - 溯源扫码入口：`android/app/src/main/java/com/wzh/suyuan/feature/trace/TraceScanActivity.java`
  - 溯源详情与物流时间线：`android/app/src/main/java/com/wzh/suyuan/feature/trace/TraceDetailActivity.java`
  - 扫码记录列表：`android/app/src/main/java/com/wzh/suyuan/feature/trace/TraceRecordActivity.java`
  - 扫码记录本地库：`android/app/src/main/java/com/wzh/suyuan/data/db/`
  - 网络模型：`android/app/src/main/java/com/wzh/suyuan/network/model/Trace*.java`
  - 页面布局：`android/app/src/main/res/layout/activity_trace_*.xml`
- 后端服务：`backend/`
  - 溯源接口：`backend/src/main/java/com/wzh/suyuan/backend/controller/TraceAdminController.java`、`TraceController.java`
  - 业务服务：`backend/src/main/java/com/wzh/suyuan/backend/service/TraceService.java`
  - 实体与仓库：`backend/src/main/java/com/wzh/suyuan/backend/entity/`、`repository/`
  - DTO 模型：`backend/src/main/java/com/wzh/suyuan/backend/dto/Trace*.java`

## 2. Android 端实现说明

### 2.1 溯源扫码入口
- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/trace/TraceScanActivity.java`
  - 集成 ZXing 扫码，失败时提示并支持手动输入
  - 支持扫码记录入口跳转

### 2.2 溯源详情与时间线
- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/trace/TraceDetailActivity.java`
  - 展示批次信息（产地、生产方、批次号、检测信息等）
  - 物流节点按时间倒序展示，空列表显示“暂无物流信息”
  - 请求失败展示错误态，支持重试

### 2.3 扫码记录
- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/trace/TraceRecordActivity.java`
  - Room 本地保存扫描记录（traceCode + 时间戳 + 产品名）
  - 列表支持点击再次进入详情

## 3. 后端实现说明

### 3.1 溯源批次与物流接口
- 管理端创建批次：`POST /admin/trace`
- 管理端新增物流节点：`POST /admin/trace/{traceCode}/logistics`
- 管理端二维码生成：`GET /admin/trace/{traceCode}/qrcode`
- 用户端溯源详情：`GET /trace/{traceCode}`

### 3.2 数据模型
- 批次表：`trace_batch`（traceCode 唯一）
- 物流表：`logistics_node`

## 4. 核心算法与逻辑

### 4.1 溯源码生成
- 规则：`TR` + 日期（yyyyMMdd）+ 4 位随机数
- 通过唯一索引与重试保证冲突时可自动生成新码

### 4.2 二维码生成
- 采用 ZXing 生成 PNG 二维码
- 二维码内容默认使用 traceCode（短码）

### 4.3 扫码记录保存
- 详情接口成功后写入本地 Room
- 记录写入失败不影响详情展示

## 5. 接口参数定义（核心）

- `POST /admin/trace`
  - Request：`{"productId":1,"origin":"山东","producer":"合作社A","batchNo":"B001",...}`
  - Response：`{"code":0,"message":"OK","data":{"id":10,"traceCode":"TR202601010001"}}`
- `GET /trace/{traceCode}`
  - Response：`{"code":0,"message":"OK","data":{"batch":{...},"logistics":[...]}}`
- `POST /admin/trace/{traceCode}/logistics`
  - Request：`{"nodeTime":"2026-02-12T10:00:00","location":"仓库A","statusDesc":"已出库"}`
  - Response：`{"code":0,"message":"OK","data":{"id":100}}`
- `GET /admin/trace/{traceCode}/qrcode`
  - Response：二维码 PNG 二进制流

## 6. 使用示例

```bash
# 管理端创建溯源批次
curl -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"productId":1,"origin":"山东","producer":"合作社A","batchNo":"B001"}' \
  http://localhost:8080/admin/trace

# 新增物流节点
curl -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"nodeTime":"2026-02-12T10:00:00","location":"仓库A","statusDesc":"已出库"}' \
  http://localhost:8080/admin/trace/TR202601010001/logistics

# 用户端查询溯源详情
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/trace/TR202601010001
```

## 7. 注意事项

- 管理端接口需具备 admin 角色权限
- 二维码内容默认使用 traceCode；如改为 URL 需同步更新扫码解析逻辑
- 扫码记录为本地存储，不做多端同步

## 8. 常见问题

- 扫码失败如何处理？
  - 页面已提供手动输入溯源码的兜底
- 物流信息为空时如何展示？
  - 详情页展示“暂无物流信息”空态
- 扫码记录未出现？
  - 需保证溯源详情加载成功才会写入记录

## 9. 测试说明

- 手工验证：扫码 -> 详情展示 -> 记录列表可回查
- 接口验证：创建批次 / 新增物流 / 查询详情流程
