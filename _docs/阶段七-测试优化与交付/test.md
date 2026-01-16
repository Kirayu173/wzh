# 阶段七测试报告（测试优化与交付）

## 1. 测试范围
- 后端：登录/注册、商品与购物车、下单/支付/取消/确认、管理端发货、溯源批次/物流/二维码接口。
- Android：静态检查（Gradle Lint），不执行自动化 UI/E2E。
- 说明：性能基线以 MockMvc + H2 采样为主；设备兼容性需线下机型补测。

## 2. 测试环境
- 操作系统：Windows
- JDK：21.0.7
- Maven：本地默认安装
- Gradle Wrapper：8.13
- Android SDK：本机默认配置
- 数据库：H2（内存模式，MySQL 兼容）

## 3. 测试方法
- 后端自动化测试：执行 `mvn test`
- Android 静态检查：执行 `.\gradlew.bat :app:lint`
- 性能采样：基于 MockMvc 请求日志（RequestLogFilter）统计 P95

## 4. 测试用例
- TC-BE-AUTH-001：注册/登录/获取用户信息
- TC-BE-PROD-001：商品列表与详情查询
- TC-BE-CART-001：购物车新增/更新/删除
- TC-BE-ORDER-001：下单/支付/取消/确认状态流转与幂等
- TC-BE-ADMIN-ORDER-001：管理员发货（PAID -> SHIPPED）
- TC-BE-ADMIN-ORDER-002：非管理员访问管理端接口被拒绝
- TC-BE-TRACE-001：创建溯源批次 + 物流节点 + 查询详情
- TC-BE-TRACE-002：二维码生成与无效溯源码异常
- TC-AND-001：Android Lint 静态检查

## 5. 测试结果
- TC-BE-AUTH-001：通过
- TC-BE-PROD-001：通过
- TC-BE-CART-001：通过
- TC-BE-ORDER-001：通过
- TC-BE-ADMIN-ORDER-001：通过
- TC-BE-ADMIN-ORDER-002：通过
- TC-BE-TRACE-001：通过
- TC-BE-TRACE-002：通过
- TC-AND-001：通过（Lint 报告生成）

## 6. 缺陷与修复
- D-001（P0/Android/构建阻断）`strings.xml` 存在缺失闭合标签，导致资源合并失败  
  - 修复：补齐字符串闭合标签并校正关键文案  
  - 位置：`android/app/src/main/res/values/strings.xml`
- D-002（P1/Android/编译）管理端编辑 Presenter 使用泛型不匹配导致编译失败  
  - 修复：使用泛型回调统一处理创建/更新响应  
  - 位置：`android/app/src/main/java/com/wzh/suyuan/feature/admin/product/AdminProductEditPresenter.java`、`android/app/src/main/java/com/wzh/suyuan/feature/admin/trace/AdminTraceEditPresenter.java`
- D-003（P1/Android/Lint）格式化字符串缺失占位符，触发 StringFormatInvalid  
  - 修复：补齐 `%1$s/%1$d` 等占位符  
  - 位置：`android/app/src/main/res/values/strings.xml`

## 7. 性能与兼容性基线
- 后端接口（MockMvc/H2 采样）：P95=94ms，Max=98ms，Avg=19.71ms，Samples=34  
  - 说明：该基线不包含网络与真实数据库开销，仅用于回归对比
- Android 兼容性：Lint 通过；不同 Android 版本与屏幕尺寸需线下设备补测

## 8. 回归测试
- 后端：修复后重新执行 `mvn test`，通过
- Android：修复后重新执行 `.\gradlew.bat :app:lint`，通过

## 9. 结论
- 阶段七核心回归用例通过，管理端关键操作与溯源链路符合 Spec/ADR 要求
- 性能基线已形成（MockMvc/H2 采样），兼容性测试待线下设备补充
