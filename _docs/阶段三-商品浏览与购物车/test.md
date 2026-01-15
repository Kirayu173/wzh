# 阶段三测试报告（商品浏览与购物车）

## 1. 测试范围
- 后端：商品与购物车接口的单元/集成测试（列表、详情、购物车增删改查）。
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
- TC-BE-001：后端商品/购物车接口测试
  - 步骤：在 `backend/` 执行 `mvn test`
  - 预期：商品列表、详情、购物车增删改查测试通过
- TC-AND-001：Android Lint 静态检查
  - 步骤：在 `android/` 执行 `./gradlew.bat :app:lint`
  - 预期：Lint 执行完成并生成报告

## 5. 测试结果
- TC-BE-001：通过
- TC-AND-001：通过（Lint 无错误）

## 6. 缺陷与修复
- D-001（P0/阻断编译）：后端新增 Java 文件包含 BOM，导致 `javac` 报 “非法字符 \ufeff”。
  - 修复：批量移除 BOM，统一为 UTF-8 无 BOM 编码。
- D-002（P0/阻断编译）：Android 新增 Java 文件包含 BOM，导致 `javac` 报 “非法字符 \ufeff”。
  - 修复：批量移除 BOM，统一为 UTF-8 无 BOM 编码。
- D-003（P3/构建警告）：`AndroidManifest.xml` 使用 `package` 属性，AGP 提示已不再支持。
  - 修复：移除 `manifest` 的 `package` 属性，改用 Gradle `namespace`。

## 7. 回归测试
- 后端：修复后重新执行 `mvn test`，通过。
- Android：修复后重新执行 `./gradlew.bat :app:lint`，通过。

## 8. 结论
- 后端商品与购物车测试通过，构建链路稳定。
- Android 静态检查链路恢复可用，Lint 报告无错误。
