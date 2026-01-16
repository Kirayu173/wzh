# 阶段七代码文档（测试优化与交付）

## 1. 代码结构概览

- 后端服务：`backend/`
  - 请求日志过滤器：`backend/src/main/java/com/wzh/suyuan/backend/config/RequestLogFilter.java`
  - 管理员鉴权复用：`backend/src/main/java/com/wzh/suyuan/backend/controller/support/AdminAuthSupport.java`
  - 管理端发货回归：`backend/src/test/java/com/wzh/suyuan/backend/AdminOrderControllerTest.java`
- Android 客户端：`android/`
  - 管理端编辑泛型回调：`android/app/src/main/java/com/wzh/suyuan/feature/admin/product/AdminProductEditPresenter.java`
  - 溯源批次编辑泛型回调：`android/app/src/main/java/com/wzh/suyuan/feature/admin/trace/AdminTraceEditPresenter.java`
  - 字符串格式修复：`android/app/src/main/res/values/strings.xml`

## 2. 功能说明

- 请求日志统一输出 requestId、endpoint、耗时与状态码，支持回归定位。
- 管理员权限校验逻辑收敛为公共方法，减少重复代码。
- 管理端编辑接口使用泛型回调统一处理创建/更新响应。
- 修复字符串资源格式，确保 Lint 与格式化调用一致。

## 3. 核心算法原理

### 3.1 RequestLogFilter
- 进入请求时读取 `X-Request-Id`，不存在则生成 UUID。
- 请求结束记录耗时与响应状态码，输出到统一日志。

### 3.2 AdminAuthSupport
- 从认证信息读取 `JwtUserPrincipal`。
- 角色非 admin 时抛出 403，保证管理端接口权限一致性。

### 3.3 泛型回调处理
- 使用 `Callback<BaseResponse<T>>` 统一处理创建/更新结果。
- 仅依赖 `BaseResponse` 状态，不绑定具体返回模型类型。

## 4. 接口参数定义（新增/补充）

- 请求头：`X-Request-Id`（可选）
  - 说明：用于前后端链路追踪；未传时由后端生成并回写到响应头。

## 5. 使用示例

```bash
# 指定 requestId 访问接口
curl -H "Authorization: Bearer <token>" -H "X-Request-Id: demo-req-001" \
  http://localhost:8080/products

# 后端回归测试
cd backend
mvn test

# Android Lint 静态检查
cd android
./gradlew :app:lint
```

## 6. 注意事项

- RequestLogFilter 输出为回归基线日志，线上需结合日志采样策略控制量级。
- 字符串格式必须保持占位符与调用参数一致，避免 StringFormatInvalid。
- 性能基线基于 MockMvc + H2 采样，仅用于回归对比。

## 7. 常见问题

- Lint 报错 StringFormatInvalid？
  - 检查 `strings.xml` 中占位符与 `getString` 传参是否一致。
- 管理端接口提示 403？
  - 确认用户角色为 admin，或检查 token 是否有效。

## 8. 测试说明

- 后端：`mvn test`
- Android：`./gradlew :app:lint`
