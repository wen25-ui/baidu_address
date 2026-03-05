# 导航管理平台 - 项目进度

> 最后更新: 2026-03-05

## 📌 项目概述

| 项目名称 | 导航管理平台 (Navigation Management Platform) |
|---------|----------------------------------------------|
| 技术栈 | Spring Boot + Vue.js + 百度地图 API |
| Java 版本 | 17 → **升级中 → 21** |
| Spring Boot | 2.7.5 |
| 构建工具 | Maven 3.8.1 |
| 数据库 | MySQL (JPA + MyBatis) |

---

## 🗂️ 模块结构

| 模块 | 说明 | 文档 |
|------|------|------|
| 用户管理 | 用户注册/登录/CRUD | [docs/modules/user.md](docs/modules/user.md) |
| 订单管理 | 订单创建/查询/删除 | [docs/modules/order.md](docs/modules/order.md) |
| 导航服务 | 路线管理/百度地图集成 | [docs/modules/navigation.md](docs/modules/navigation.md) |
| 计费管理 | 计费记录/费用计算 | [docs/modules/billing.md](docs/modules/billing.md) |
| 前端 | Vue.js SPA 界面 | [docs/modules/frontend.md](docs/modules/frontend.md) |

---

## 📅 变更日志

### 2026-03-05

#### 🔧 编译错误修复（Java 21 升级前置准备）

- [x] 创建缺失的 `UserDTO` 类 (`model/dto/UserDTO.java`)
- [x] 重命名 `ExceptionHandler` → `GlobalExceptionHandler`（解决类名与注解冲突）
- [x] 添加 `Gson` 依赖到 `pom.xml`（`BaiduMapUtil` 需要）
- [x] 修复 `BaiduMapConfig` — 移除不存在的 `com.baidu.mapapi.SDKInitializer`
- [x] 修复 `UserService` 接口 — 与 `UserServiceImpl` 方法签名对齐
- [x] 修复 `NavigationService` 接口 — 与 `NavigationServiceImpl` 方法签名对齐
- [x] 修复 `BillingService` 接口 — 与 `BillingServiceImpl` 方法签名对齐
- [x] 修复 `OrderService` 接口 — 使用 `Order` 实体替代 `OrderVO` 返回类型
- [x] 修复 `OrderController` — 与 `OrderService` 新接口对齐
- [x] 修复 `NavigationController` — 与 `NavigationService` 新接口对齐
- [x] 修复 `BillingController` — 与 `BillingService` 新接口对齐
- [x] 修复 `BillingUtil` — 添加 `convertToBillingRecord` 和 `updateBillingRecord` 方法
- [x] 修复 `UserServiceImpl` — 添加缺失的 `login` 方法实现
- [x] 添加 Spring Boot Parent POM (`spring-boot-starter-parent:2.7.5`)
- [x] 添加 `maven-compiler-plugin` 配置
- [x] Maven 仓库配置 — 禁用不可用的 Nexus 镜像，启用阿里云镜像

#### ✅ 构建状态: **编译通过**

#### 🚀 Java 21 升级（进行中）

- [x] 升级计划生成（会话 ID: 20260305033800）
- [ ] 升级环境配置
- [ ] `pom.xml` Java 版本变更 17 → 21
- [ ] Spring Boot 版本升级（如需要）
- [ ] `javax.*` → `jakarta.*` 迁移（如涉及 Spring Boot 3.x）
- [ ] 废弃 API 处理
- [ ] 编译验证
- [ ] 测试验证

---

## 🏗️ 待办事项

- [ ] Java 21 升级完成
- [ ] 百度地图 AK 配置（部署时需要）
- [ ] 数据库配置完善
- [ ] 单元测试编写
- [ ] 前端联调
- [ ] 部署配置
