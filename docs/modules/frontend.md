# 前端模块

> 最后更新: 2026-03-05

## 模块说明

前端使用 Vue.js 构建的单页面应用（SPA），集成百度地图实现可视化导航。

## 技术栈

| 技术 | 说明 |
| ---- | ---- |
| Vue.js | 前端框架 |
| Vite | 构建工具 |
| Vue Router | 路由管理 |
| Vuex | 状态管理 |
| Axios | HTTP 请求 |
| 百度地图 JS API | 地图可视化 |

## 文件清单

### 页面视图 (`views/`)

| 文件 | 说明 |
| ---- | ---- |
| `Login.vue` | 登录页面 |
| `Dashboard.vue` | 仪表盘 |
| `UserManagement.vue` | 用户管理 |
| `OrderManagement.vue` | 订单管理 |
| `NavigationMap.vue` | 导航地图 |
| `BillingManagement.vue` | 计费管理 |
| `PlatformRevenue.vue` | 平台营收 |

### 组件 (`components/`)

| 文件 | 说明 |
| ---- | ---- |
| `Header.vue` | 顶部导航栏 |
| `Sidebar.vue` | 侧边栏菜单 |
| `BaiduMap.vue` | 百度地图组件 |
| `OrderTable.vue` | 订单表格 |
| `BillingDetail.vue` | 计费详情 |
| `RealTimeNavigation.vue` | 实时导航 |

### API 调用 (`api/`)

| 文件 | 对应后端模块 |
| ---- | ---- |
| `user.js` | 用户管理 |
| `order.js` | 订单管理 |
| `navigation.js` | 导航服务 |
| `billing.js` | 计费管理 |

### 工具 (`utils/`)

| 文件 | 说明 |
| ---- | ---- |
| `request.js` | Axios 请求封装 |
| `auth.js` | 认证工具 |
| `map.js` | 地图工具 |

## 变更记录

### 2026-03-05

- 初始记录，暂无变更
