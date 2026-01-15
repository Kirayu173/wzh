# 阶段一测试报告（项目初始化与架构搭建）

## 1. 测试范围
- 后端：Maven 构建与单元测试（编译、依赖解析、测试框架执行）。
- Android：仅静态检查/构建流程验证（Gradle Lint 触发与配置检查）。
- 说明：本阶段不包含业务接口联调与 UI 自动化测试。

## 2. 测试环境
- 操作系统：Windows
- JDK：21.0.7（后端构建），17.0.12（Android Gradle 运行用）
- Maven：本地默认安装
- Gradle Wrapper：7.5.1
- Android SDK：未配置（导致 Lint 无法执行）

## 3. 测试方法
- 构建与编译验证：执行 `mvn test`，验证后端代码可编译与依赖可解析。
- 静态检查：尝试执行 `./gradlew :app:lint`，验证 Android 工程静态检查链路。
- 编码与工具链兼容性检查：针对 JDK/Gradle/AGP 兼容性进行验证与修复。

## 4. 测试用例
- TC-BE-001：后端构建与测试
  - 步骤：在 `backend/` 执行 `mvn test`
  - 预期：编译与测试阶段通过
- TC-AND-001：Android Lint 触发
  - 步骤：在 `android/` 执行 `./gradlew :app:lint`
  - 预期：静态检查任务可执行并生成报告

## 5. 测试结果
- TC-BE-001：通过
- TC-AND-001：失败（缺少 Android SDK，Lint 无法执行）

## 6. 缺陷与修复
- D-001（P0/阻断构建）：后端 Java 文件含 BOM，编译报 “非法字符 \ufeff”。
  - 修复：统一为 UTF-8 无 BOM 编码。
- D-002（P0/阻断构建）：Lombok 与当前 JDK 不兼容，编译报 `NoSuchFieldError`。
  - 修复：升级 Lombok 至 1.18.30，并显式设置 `maven-compiler-plugin` 的 `release=11`。
- D-003（P1/工具链兼容）：Gradle 5.4.1 + AGP 3.5.0 与 JDK 21 不兼容，Lint 启动失败。
  - 修复：升级 Gradle Wrapper 至 7.5.1、AGP 至 7.4.2，并补充 `namespace` 配置。
- D-004（P2/环境问题）：Android SDK 未配置，Lint 无法执行。
  - 修复：未修复（需在本地安装并配置 Android SDK）。

## 7. 回归测试
- 后端：修复后重新执行 `mvn test`，通过。
- Android：重新执行 `./gradlew :app:lint`，仍因缺少 Android SDK 失败（环境阻断）。

## 8. 结论
- 后端构建与测试通过，基础架构可编译运行。
- Android 静态检查链路已修复工具链兼容性问题，但受 SDK 环境限制，Lint 未完成。