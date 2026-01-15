# 阶段四测试报告（订单与支付功能）

## 1. 测试范围
- 后端：地址管理、订单创建/支付/取消/确认收货接口及状态流转。
- Android：仅静态检查（Gradle Lint），不执行功能性测试与 UI 自动化测试。
- 说明：不包含真实支付、管理端发货等超出阶段范围能力。

## 2. 测试环境
- 操作系统：Windows
- JDK：21.0.7（Maven 与 Gradle 运行环境）
- Maven：本地默认安装
- Gradle Wrapper：8.13
- Android SDK：`D:\Application\AndroidStudio_SDK`

## 3. 测试方法
- 后端自动化测试：执行 `mvn test` 验证接口与状态流转。
- Android 静态检查：执行 `.\gradlew.bat :app:lint` 生成 Lint 报告。

## 4. 测试用例
- TC-BE-ADDR-001：地址默认逻辑
  - 步骤：创建多条地址并设置默认，查询列表验证默认唯一
  - 预期：仅一条地址为默认
- TC-BE-ORDER-001：订单创建/支付/取消/确认
  - 步骤：购物车选中项下单，支付成功，取消已支付订单，确认收货（待收货）
  - 预期：支付成功后订单状态为 `PAID`；取消已支付订单返回 409；确认收货仅在 `SHIPPED` 成功
- TC-BE-ORDER-002：非法状态操作
  - 步骤：对非 `SHIPPED` 状态执行确认收货
  - 预期：返回 409，订单状态不变
- TC-BE-ORDER-003：下单幂等控制
  - 步骤：使用相同 `requestId` 重复提交订单
  - 预期：返回同一订单 id，库存不重复扣减
- TC-AND-001：Android Lint 静态检查
  - 步骤：在 `android/` 执行 `./gradlew.bat :app:lint`
  - 预期：Lint 执行完成并生成报告

## 5. 测试结果
- TC-BE-ADDR-001：通过
- TC-BE-ORDER-001：通过
- TC-BE-ORDER-002：通过
- TC-BE-ORDER-003：通过
- TC-AND-001：通过（Lint 报告生成）

## 6. 缺陷与修复
- D-001（P1/构建阻断/资源缺失）：新增订单页面引用 `label_address`，Android 资源链接失败。
  - 修复：补充字符串资源 `label_address`。
  - 位置：`android/app/src/main/res/values/strings.xml`
- D-002（P1/功能缺陷/状态机）：订单确认收货在 `PAID` 状态下可执行，与阶段四状态机不一致。
  - 修复：后端限制仅 `SHIPPED` 可确认收货；Android UI 同步仅在 `SHIPPED` 显示确认按钮；新增测试覆盖非法状态。
  - 位置：`backend/src/main/java/com/wzh/suyuan/backend/service/OrderService.java`、`android/app/src/main/java/com/wzh/suyuan/feature/order/OrderDetailActivity.java`、`backend/src/test/java/com/wzh/suyuan/backend/OrderAddressControllerTest.java`

## 7. 回归测试
- 后端：修复后重新执行 `mvn test`，通过。
- Android：修复后重新执行 `./gradlew.bat :app:lint`，通过。

## 8. 结论
- 阶段四核心接口测试通过，订单状态流转符合 spec/adr 要求。
- Android 静态检查通过，构建链路稳定。
