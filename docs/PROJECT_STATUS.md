# 私家车位共享平台 - 项目状态文档

> 最后更新：2026年3月15日 18:50

---

## 一、项目概述

### 1.1 项目目标
实现"车位主人发布车位用户搜索预约在线下单使用核销订单结算"完整闭环的私家车位共享平台。

### 1.2 技术栈
| 层级 | 技术选型 | 状态 |
|-----|---------|------|
| 后端框架 | Spring Boot 3.2.0 | ✅ |
| ORM框架 | MyBatis Plus 3.5.5 | ✅ |
| 数据库 | MySQL 8.0 | ✅ |
| 缓存 | Redis | 配置完成 |
| 前端（小程序） | 微信小程序 / uni-app | ✅ 核心页面已完成 |
| 前端（管理端） | Vue 3 + Vite + Element Plus | 待完善 |
| 地图服务 | 百度地图API | ✅ 配置完成 |
| 认证 | JWT (jjwt 0.12.3) | ✅ |
| 工具包 | Hutool 5.8.24 | ✅ |
| Java版本 | 17 | ✅ |

### 1.3 数据库配置
- **数据库名**: graduation_project
- **主机**: localhost
- **端口**: 3306
- **用户名**: root
- **密码**: 123456
- **状态**: ✅ 已初始化（9张表 + 管理员种子数据）

### 1.4 联调状态 ✅
- **后端服务**: http://localhost:8080 正常运行
- **前端编译**: `npm run dev:mp-weixin` 编译成功
- **接口连通**: 健康检查API通过

---

## 二、已完成工作

### 2.1 Phase 1: 基础框架搭建 ✅

#### 数据库
- [x] 创建数据库 graduation_project
- [x] 初始化9张业务表
- [x] 插入管理员种子数据

#### 后端依赖
- [x] pom.xml 更新（Spring Boot 3.2.0, MyBatis Plus 3.5.5）
- [x] application.yml 配置（数据库、百度地图、JWT、Redis）

#### 实体类（9个）
- [x] User - 用户（支持车主/车位主人双重身份）
- [x] ParkingSpace - 车位信息
- [x] ParkingSpaceRule - 车位可用时段规则
- [x] Reservation - 预约订单
- [x] Wallet - 用户钱包
- [x] Transaction - 交易流水
- [x] Admin - 管理员
- [x] UserFavorite - 用户收藏
- [x] CreditRecord - 信用记录

#### Mapper接口（9个）
- [x] UserMapper, ParkingSpaceMapper, ParkingSpaceRuleMapper
- [x] ReservationMapper, WalletMapper, TransactionMapper
- [x] AdminMapper, UserFavoriteMapper, CreditRecordMapper

#### 配置类
- [x] MybatisPlusConfig - 分页、乐观锁插件
- [x] SecurityConfig - Spring Security 6.x 配置
- [x] WebConfig - CORS跨域配置
- [x] JwtProperties, BaiduMapProperties, ParkingRulesProperties

#### 公共组件
- [x] Result - 统一响应封装（支持分页）
- [x] ResultCode - 响应状态码枚举
- [x] BusinessException - 业务异常
- [x] GlobalExceptionHandler - 全局异常处理

#### 工具类
- [x] JwtUtils - JWT令牌生成/解析
- [x] OrderNoUtils - 订单号生成（雪花算法）

#### 控制器
- [x] HealthController - 健康检查接口

### 2.2 Phase 2: 用户模块 ✅

#### Service层
- [x] UserService 接口定义
- [x] UserServiceImpl 实现（微信登录、用户管理、信用分管理）

#### 控制器
- [x] UserController - 用户API
  - POST `/api/v1/user/wx-login` - 微信登录
  - GET `/api/v1/user/info` - 获取用户信息
  - PUT `/api/v1/user/info` - 更新用户信息
  - POST `/api/v1/user/verify` - 实名认证
  - GET `/api/v1/user/credit-check` - 信用分检查

#### 安全组件
- [x] JwtAuthenticationFilter - JWT认证过滤器
- [x] JwtUtils 扩展 - getUserIdFromToken, getRoleFromToken
- [x] SecurityConfig 更新 - 集成JWT过滤器

#### 配置修复
- [x] WebConfig - CORS配置修复（allowedOriginPatterns）

### 2.3 Phase 3: 车位模块 ✅

#### Service层
- [x] ParkingSpaceService 接口定义
- [x] ParkingSpaceServiceImpl 实现（发布、上下架、搜索、统计）
- [x] ParkingSpaceRuleService 接口定义
- [x] ParkingSpaceRuleServiceImpl 实现（时段规则管理）

#### 控制器
- [x] ParkingSpaceController - 车位API
  - POST `/api/v1/parking` - 发布车位
  - GET `/api/v1/parking/{id}` - 获取车位详情
  - PUT `/api/v1/parking/{id}` - 更新车位
  - DELETE `/api/v1/parking/{id}` - 删除车位
  - POST `/api/v1/parking/{id}/online` - 上架
  - POST `/api/v1/parking/{id}/offline` - 下架
  - GET `/api/v1/parking/nearby` - 搜索附近车位（Haversine算法）
  - GET `/api/v1/parking/search` - 关键词搜索
  - GET `/api/v1/parking/mine` - 我的车位列表
  - GET `/api/v1/parking/stats` - 车位统计
  - POST `/api/v1/parking/{spaceId}/rules` - 添加时段规则
  - GET `/api/v1/parking/{spaceId}/rules` - 获取时段规则
  - GET `/api/v1/parking/{spaceId}/available` - 查询可用时段
  - DELETE `/api/v1/parking/rules/{ruleId}` - 删除时段规则

#### 核心算法
- [x] Haversine公式 - 地球表面两点距离计算（EARTH_RADIUS=6371km）

### 2.4 Phase 4: 预约与订单 ✅

#### Service层
- [x] ReservationService 接口定义
- [x] ReservationServiceImpl 实现
  - 创建预约订单（时段冲突检测）
  - 支付订单（余额扣除、流水记录）
  - 取消订单（已支付自动退款）
  - 核销订单（验证码校验）
  - 完成订单（结算给车位主人，扣除10%服务费）
  - 超时订单处理（15分钟未支付自动取消）
  - 自动完成订单（结束时间后自动完成）

#### 控制器
- [x] ReservationController - 预约API
  - POST `/api/v1/reservation` - 创建预约
  - GET `/api/v1/reservation/{id}` - 获取订单详情
  - GET `/api/v1/reservation/no/{orderNo}` - 根据订单号查询
  - POST `/api/v1/reservation/{id}/pay` - 支付订单
  - POST `/api/v1/reservation/{id}/cancel` - 取消订单
  - POST `/api/v1/reservation/{id}/verify` - 核销订单
  - POST `/api/v1/reservation/{id}/complete` - 完成订单
  - GET `/api/v1/reservation/my` - 我的订单列表
  - GET `/api/v1/reservation/received` - 收到的订单列表
  - GET `/api/v1/reservation/space/{spaceId}` - 车位订单列表
  - GET `/api/v1/reservation/check-available` - 检查时段可用性
  - GET `/api/v1/reservation/stats/user` - 用户订单统计
  - GET `/api/v1/reservation/stats/owner` - 车位主人订单统计

#### 配置
- [x] OrderScheduleConfig - 订单定时任务（超时处理、自动完成）

### 2.5 Phase 5: 钱包与支付 ✅

#### Service层
- [x] WalletService 接口定义
- [x] WalletServiceImpl 实现（充值、提现、余额管理、交易流水）

#### 控制器
- [x] WalletController - 钱包API
  - GET `/api/v1/wallet` - 获取钱包信息
  - POST `/api/v1/wallet/recharge` - 充值
  - POST `/api/v1/wallet/withdraw` - 提现
  - GET `/api/v1/wallet/transactions` - 交易记录
  - GET `/api/v1/wallet/stats` - 钱包统计

### 2.6 Phase 6: 管理端 ✅

#### Service层
- [x] AdminService 接口定义
- [x] AdminServiceImpl 实现（登录认证、用户管理、车位审核、数据统计）

#### 控制器
- [x] AdminController - 管理端API
  - POST `/api/admin/login` - 管理员登录
  - GET `/api/admin/info` - 获取管理员信息
  - POST `/api/admin/change-password` - 修改密码
  - GET `/api/admin/users` - 用户列表
  - PUT `/api/admin/users/{userId}/status` - 更新用户状态
  - GET `/api/admin/parking-spaces` - 车位列表
  - POST `/api/admin/parking-spaces/{spaceId}/audit` - 车位审核
  - POST `/api/admin/parking-spaces/{spaceId}/force-offline` - 强制下架
  - GET `/api/admin/reservations` - 订单列表
  - GET `/api/admin/reservations/{id}` - 订单详情
  - GET `/api/admin/stats/platform` - 平台统计
  - GET `/api/admin/stats/income` - 收入统计

### 2.7 Phase 7: 收藏与信用 ✅

#### Service层
- [x] UserFavoriteService 接口定义
- [x] UserFavoriteServiceImpl 实现（添加/取消收藏、收藏列表）
- [x] CreditRecordService 接口定义
- [x] CreditRecordServiceImpl 实现（信用分管理、信用记录）

#### 控制器
- [x] UserFavoriteController - 收藏API
  - POST `/api/v1/favorite/{spaceId}` - 添加收藏
  - DELETE `/api/v1/favorite/{spaceId}` - 取消收藏
  - GET `/api/v1/favorite/{spaceId}/check` - 检查收藏状态
  - GET `/api/v1/favorite/list` - 收藏列表
  - GET `/api/v1/favorite/count` - 收藏数量

- [x] CreditRecordController - 信用分API
  - GET `/api/v1/credit/score` - 获取信用分
  - GET `/api/v1/credit/records` - 信用记录
  - GET `/api/v1/credit/check` - 信用检查

### 2.8 应用状态
✅ **后端核心功能全部完成** - 59个源文件编译通过

### 2.9 前端页面补全 ✅
- [x] 发布车位、我的车位
- [x] 收到订单、订单核销
- [x] 收藏列表、信用分
- [x] 个人信息、实名认证
- [x] 交易记录
- [x] 登录API补齐（管理员登录）

---

## 三、待完成工作

### 后端开发 ✅ 已完成
所有后端模块开发完成！

### 前端开发 ⬅️ 下一阶段
- [x] 用户侧核心页面补全
- [ ] 管理端页面与数据统计优化
- [ ] 静态资源补齐（图标/占位图）

---

## 四、API接口规范

### 4.1 基础路径
- 小程序API: /api/v1/
- 管理端API: /api/admin/
- 健康检查: /api/health

### 4.2 响应格式
\\\json
{
    "code": 200,
    "message": "success",
    "data": {},
    "timestamp": 1710403200000
}
\\\

---

## 五、变更记录

详见 docs/Amendment/ 目录
