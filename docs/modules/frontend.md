# 前端模块

> 最后更新: 2026-03-05

## 模块说明

前端使用 **uni-app (Vue 3)** 构建的微信小程序，使用微信原生地图组件实现导航可视化。

## 技术栈

| 技术 | 说明 |
| ---- | ---- |
| uni-app | 跨平台框架（Vue 3 版本） |
| Vite | 构建工具 |
| Vuex | 状态管理 |
| uni.request | HTTP 请求（替代 Axios） |
| 微信地图组件 | 地图可视化（替代百度地图 JS API） |

## 项目结构

```
frontend/
 package.json           # 依赖配置
 vite.config.js         # Vite + uni 插件配置
 index.html             # H5 入口
 src/
    main.js            # 应用入口
    App.vue            # 根组件 + 全局样式
    pages.json         # 路由 + tabBar 配置
    manifest.json      # 小程序配置
    pages/             # 页面目录
       login/login.vue
       dashboard/dashboard.vue
       order/order.vue
       navigation/navigation.vue
       billing/billing.vue
       user/user.vue
       revenue/revenue.vue
    api/               # 后端 API 接口
       user.js
       order.js
       navigation.js
       billing.js
    store/index.js     # Vuex 状态管理
    utils/
       request.js     # uni.request 封装
       auth.js        # 认证/存储工具
    static/            # 静态资源
 dist/build/mp-weixin/  # 编译产物（微信开发者工具导入此目录）
```

## 页面说明

| 页面 | 路径 | 说明 |
| ---- | ---- | ---- |
| 登录 | pages/login/login | 用户名密码登录 |
| 仪表盘 | pages/dashboard/dashboard | 平台概览 + 快捷操作 + 最近订单 |
| 订单管理 | pages/order/order | 订单列表 + 查看/取消 |
| 实时导航 | pages/navigation/navigation | 微信地图 + 路线选择 + 定位 |
| 计费管理 | pages/billing/billing | 计费概览 + 记录列表 |
| 我的 | pages/user/user | 个人资料 + 用户管理 + 退出 |
| 平台收入 | pages/revenue/revenue | 收入概览 + 明细 |

## 运行方式

```bash
# 1. 安装依赖
cd frontend && npm install

# 2. 编译微信小程序
npm run build:mp-weixin

# 3. 在微信开发者工具中导入 dist/build/mp-weixin 目录
```

## 变更记录

### 2026-03-05

- 前端重构：Vue.js + Vite Web 应用  uni-app 微信小程序
- 替换 Axios  uni.request
- 替换 Vue Router  pages.json 路由
- 替换 Element Plus  小程序原生组件
- 替换百度地图  微信 map 组件
- 替换 localStorage  uni.getStorageSync
- 编译通过，产物已就绪
