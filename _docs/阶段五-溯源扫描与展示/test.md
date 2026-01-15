# 阶段五测试报告（溯源扫描与展示）

## 1. 测试范围
- 后端：溯源批次创建、溯源详情查询、物流节点新增、二维码生成与异常处理
- Android：仅静态检查（Gradle Lint），不执行功能/UI 自动化测试
- 说明：不包含第三方物流接口、管理端 UI（阶段六）

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

## 4. 测试用例
- TC-BE-TRACE-001：管理员创建溯源批次
  - 步骤：调用 `POST /admin/trace`，提交 productId/origin/producer/batchNo
  - 预期：返回 traceCode 与 batchId
- TC-BE-TRACE-002：新增物流节点并查询详情
  - 步骤：调用 `POST /admin/trace/{traceCode}/logistics` 两次后查询 `GET /trace/{traceCode}`
  - 预期：返回批次信息与物流列表，节点按时间倒序
- TC-BE-TRACE-003：二维码生成
  - 步骤：调用 `GET /admin/trace/{traceCode}/qrcode`
  - 预期：返回 image/png
- TC-BE-TRACE-004：无效溯源码查询
  - 步骤：调用 `GET /trace/UNKNOWN`
  - 预期：返回 404
- TC-AND-001：Android Lint 静态检查
  - 步骤：执行 `.\gradlew.bat :app:lint`
  - 预期：Lint 执行完成并生成报告

## 5. 测试结果
- TC-BE-TRACE-001：通过
- TC-BE-TRACE-002：通过
- TC-BE-TRACE-003：通过
- TC-BE-TRACE-004：通过
- TC-AND-001：通过（Lint 报告生成）

## 6. 缺陷与修复
- D-001（P1/后端/数据表）溯源批次表在 H2 下未创建，导致测试初始化失败  
  - 修复：将 `process_info` 字段从 `columnDefinition=TEXT` 调整为 `length=2000`  
  - 位置：`backend/src/main/java/com/wzh/suyuan/backend/entity/TraceBatch.java`
- D-002（P1/Android/兼容性）溯源详情排序使用 `Comparator.comparing` 与 `List.sort`，低于 API 24 设备报错  
  - 修复：改为 `Collections.sort` + 自定义比较器  
  - 位置：`android/app/src/main/java/com/wzh/suyuan/feature/trace/TraceDetailPresenter.java`
- D-003（P1/Android/Manifest）CAMERA 权限未声明 `uses-feature`，Lint 报错  
  - 修复：新增 `<uses-feature android:name="android.hardware.camera" android:required="false" />`  
  - 位置：`android/app/src/main/AndroidManifest.xml`
- D-004（P2/Android/静态质量）Lint 提示 NotifyDataSetChanged、Overdraw、ContentDescription、HardcodedText、SetTextI18n、TextFields/Autofill、UnusedResources、ButtonStyle 等问题  
  - 修复：补充资源字符串/可访问性描述，移除多余背景，补全输入类型与 autofill 提示，按钮样式标准化，并对全量刷新做抑制说明  
  - 位置：`android/app/src/main/java/com/wzh/suyuan/feature/**`、`android/app/src/main/res/layout/**`、`android/app/src/main/res/values/strings.xml`
- D-005（P1/Android/资源编译）错误使用 `android:style` 导致资源链接失败  
  - 修复：改用 `style` 属性  
  - 位置：`android/app/src/main/res/layout/activity_order_detail.xml`、`android/app/src/main/res/layout/item_address.xml`

## 7. 回归测试
- 后端：修复后重新执行 `mvn test`，通过
- Android：修复后重新执行 `.\gradlew.bat :app:lint`，通过

## 8. 结论
- 阶段五核心接口与流程测试通过，溯源批次、物流节点、二维码生成符合 Spec/ADR 要求
- Android 静态检查通过，当前阶段新增页面与资源符合基础质量要求
