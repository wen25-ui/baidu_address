# 前端模块

> 最后更新: 2026-03-05

## 模块说明

前端使用 Vue 3 + Vite 构建的单页面应用（SPA），集成百度地图实现可视化导航。

## 技术栈

| 技术 | 版本 | 说明 |
| ---- | ---- | ---- |
| Vue.js | ^3.3.0 | 前端框架 |
| Vite | ^5.0.0 | 构建工具 |
| Vue Router | ^4.2.0 | 路由管理 |
| Vuex | ^4.1.0 | 状态管理 |
| Axios | ^1.6.0 | HTTP 请求 |
| Element Plus | ^2.4.0 | UI 组件库 |
| 百度地图 JS API | - | 地图可视化 |

## 运行方式

```bash
cd frontend
npm install
npm run serve    # 开发环境
npm run build    # 生产构建
npm run preview  # 预览构建产物
```

开发服务器默认运行在 `http://localhost:3000`，API 请求通过 Vite 代理转发到后端 `http://localhost:8080`。

## 文件清单

### 页面视图 (`views/`)

| 文件 | 路由 | 说明 |
| ---- | ---- | ---- |
| `Login.vue` | `/` | 登录页面 |
| `Dashboard.vue` | `/dashboard` | 仪表盘 |
| `UserManagement.vue` | `/users` | 用户管理 |
| `OrderManagement.vue` | `/orders` | 订单管理 |
| `NavigationMap.vue` | `/navigation` | 导航地图 |
| `BillingManagement.vue` | `/billing` | 计费管理 |
| `PlatformRevenue.vue` | `/revenue` | 平台营收 |

### 组件 (`components/`)

| 文件 | 说明 |
| ---- | ---- |
| `Header.vue` | 顶部导航栏 |
| `Sidebar.vue` | 侧边栏菜单 |
| `BaiduMap.vue` | 百度地图组件 |
| `OrderTable.vue` | 订单表格（支持 props 传入数据） |
| `BillingDetail.vue` | 计费详情 |
| `RealTimeNavigation.vue` | 实时导航 |

### API 调用 (`api/`)

| 文件 | 对应后端模块 | 导出方法 |
| ---- | ---- | ---- |
| `user.js` | 用户管理 | `login`, `register`, `getUserInfo`, `getUsers`, `updateUser`, `deleteUser` |
| `order.js` | 订单管理 | `createOrder`, `getOrderList`, `getOrderDetail`, `updateOrderStatus`, `deleteOrder` |
| `navigation.js` | 导航服务 | `getRoutes`, `startNavigation`, `endNavigation`, `getNavigationStatus` |
| `billing.js` | 计费管理 | `getBillingRecords`, `createBillingRecord`, `updateBillingRecord`, `deleteBillingRecord`, `fetchRevenueData` |

### 工具 (`utils/`)

| 文件 | 说明 |
| ---- | ---- |
| `request.js` | Axios 请求封装（含 Token 拦截器） |
| `auth.js` | 认证工具（Token 存取） |
| `map.js` | 百度地图工具函数 |

## 变更记录

### 2026-03-05 — 前端整改为 Vue 3 Web 应用

- 升级所有依赖到稳定版本（Vue 3.3、Vite 5、Element Plus 2.4）
- Vue 2 → Vue 3 语法全面迁移（router、store、main.js）
- 统一 API 请求层，添加 Token 认证拦截器
- 修复所有组件和页面中的 API 调用方式
- 修复路由路径一致性
- 添加 `@` 路径别名
- 登录页独立布局，其他页面使用 Header + Sidebar 布局
