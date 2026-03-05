# Navigation Management Platform

## 项目简介
本项目是一个基于Vue和Spring Boot的管理平台，集成了百度地图，支持实时导航功能。平台采用计费模式，平台抽成20%，定价为每小时6-10元。该项目不涉及硬件，主要用于管理用户、订单和计费信息。

## 技术栈
- 前端：Vue.js
- 后端：Spring Boot
- 地图服务：百度地图

## 项目结构
```
navigation-management-platform
├── frontend                # 前端代码
│   ├── public             # 公共文件
│   ├── src                # 源代码
│   ├── package.json       # npm配置文件
│   └── vite.config.js     # Vite配置文件
├── backend                 # 后端代码
│   ├── src                # 源代码
│   └── pom.xml            # Maven配置文件
└── README.md              # 项目文档
```

## 功能模块
1. **用户管理**：用户的注册、登录、信息管理。
2. **订单管理**：订单的创建、查询、管理。
3. **导航管理**：集成百度地图，提供实时导航功能。
4. **计费管理**：展示和管理计费信息，支持平台抽成。

## 安装与运行
### 前端
1. 进入前端目录：
   ```
   cd frontend
   ```
2. 安装依赖：
   ```
   npm install
   ```
3. 启动开发服务器：
   ```
   npm run dev
   ```

### 后端
1. 进入后端目录：
   ```
   cd backend
   ```
2. 使用Maven构建项目：
   ```
   mvn clean install
   ```
3. 启动Spring Boot应用：
   ```
   mvn spring-boot:run
   ```

## 贡献
欢迎任何形式的贡献！请提交问题或拉取请求。

## 许可证
本项目采用MIT许可证，详细信息请查看LICENSE文件。