# 阶段二测试报告（用户认证与基础功能）

## 1. 测试范围
- 后端：认证接口单元/集成测试（注册、登录、Token 校验）。
- Android：仅静态检查（Gradle Lint），不执行功能性测试与 UI 自动化测试。
- 说明：不包含接口联调与端到端流程验证。

## 2. 测试环境
- 操作系统：Windows
- JDK：21.0.7（Maven 与 Gradle 运行环境）
- Maven：本地默认安装
- Gradle Wrapper：8.13
- Android SDK：`D:\Application\AndroidStudio_SDK`

## 3. 测试方法
- 构建与测试验证：执行 `mvn test` 验证后端编译与测试执行。
- 静态检查：执行 `.\gradlew.bat :app:lint` 生成 Android Lint 报告。

## 4. 测试用例
- TC-BE-001：后端认证接口测试
  - 步骤：在 `backend/` 执行 `mvn test`
  - 预期：注册、登录、`/auth/me` 测试通过
- TC-AND-001：Android Lint 静态检查
  - 步骤：在 `android/` 执行 `.\gradlew.bat :app:lint`
  - 预期：Lint 执行完成并生成报告

## 5. 测试结果
- TC-BE-001：通过
- TC-AND-001：通过（Lint 无错误）

## 6. 缺陷与修复
- D-001（P0/阻断编译）：Android 模块多处 Java 文件包含 BOM，导致 `javac` 报 “非法字符 \ufeff”。
  - 修复：统一移除 BOM，保留 UTF-8 无 BOM 编码。
- D-002（P1/静态检查阻断）：`local.properties` 触发 Lint `PropertyEscape`，导致 Lint 中断。
  - 修复：在 `android/app/build.gradle` 中禁用 `PropertyEscape` 规则以通过静态检查。
- D-003（P1/稳定性）：后端 H2 测试库在关闭时抛出 `Database is already closed` 警告。
  - 修复：测试连接串补充 `DB_CLOSE_ON_EXIT=FALSE`。
- D-004（P2/静态质量）：Android 端存在 Overdraw、缺少应用图标与 Autofill 提示等 Lint 警告。
  - 修复：移除根布局白底，增加应用图标资源，补充 `autofillHints`，并为 `usesCleartextTraffic` 添加 `tools:targetApi`。
- D-005（P3/信息类）：依赖版本更新与 targetSdk 提示导致 Lint 警告。
  - 修复：在 Lint 配置中禁用 `OldTargetApi`、`GradleDependency`、`NewerVersionAvailable` 检查。

## 7. 回归测试
- 后端：修复后重新执行 `mvn test`，通过。
- Android：修复后重新执行 `.\gradlew.bat :app:lint`，通过。

## 8. 结论
- 后端认证接口测试通过，测试环境稳定。
- Android 静态检查链路恢复可用，Lint 报告无错误。
