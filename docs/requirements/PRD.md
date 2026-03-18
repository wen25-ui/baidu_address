# 私家车位共享平台 - 产品需求文档 (PRD)

> 版本：V1.0  
> 创建日期：2026年3月14日  
> 项目代号：ParkingShare

---

## 一、项目概述

### 1.1 项目背景
城市"私家车位闲置、车主停车难"问题日益突出，本平台旨在实现"车位主人发布车位→用户搜索预约→在线下单→使用核销→订单结算"的完整闭环，提高车位资源利用率。

### 1.2 项目价值
- **实际应用价值**：解决停车难痛点，契合智慧交通与共享经济发展需求
- **学习实践价值**：覆盖前后端开发、数据库设计、地图服务集成等技术
- **社会服务价值**：缓解城市停车压力，为静态交通数字化提供轻量化方案

### 1.3 项目名称
**私家车位共享平台**

---

## 二、技术架构

### 2.1 技术栈

| 层级 | 技术选型 |
|-----|---------|
| 后端框架 | Spring Boot 3.x |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| 前端（小程序） | 微信小程序原生 / uni-app |
| 前端（管理端） | Vue 3 + Vite + Element Plus |
| 地图服务 | 百度地图API |

### 2.2 百度地图配置

| 端 | AK |
|---|-----|
| 微信小程序 | ZmaKnx0vJcB745El7rFTFdqadqxM5JXt |
| 浏览器端 | Ysj2vZPKWz8nwGRiPYD7MBBOvVtQiGlt |

### 2.3 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端                                │
│  ┌─────────────────┐          ┌─────────────────────────┐   │
│  │   微信小程序     │          │     Web管理端            │   │
│  │  (车主/车位主人) │          │   (管理员/运营人员)       │   │
│  └────────┬────────┘          └───────────┬─────────────┘   │
└───────────┼───────────────────────────────┼─────────────────┘
            │                               │
            ▼                               ▼
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway                              │
└─────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Spring Boot 后端服务                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │用户服务   │ │车位服务   │ │预约服务   │ │订单服务   │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                    │
│  │信用服务   │ │地图服务   │ │通知服务   │                    │
│  └──────────┘ └──────────┘ └──────────┘                    │
└─────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────┐
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐   │
│  │    MySQL     │    │    Redis     │    │  百度地图API  │   │
│  └──────────────┘    └──────────────┘    └──────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、用户角色定义

### 3.1 角色说明

| 角色 | 说明 | 使用端 |
|-----|------|-------|
| 车主 | 搜索、预约、使用车位的用户 | 微信小程序 |
| 车位主人 | 发布、管理自有车位的用户 | 微信小程序 |
| 管理员 | 系统最高权限，全功能管理 | Web管理端 |
| 运营人员 | 日常运营，审核/订单处理 | Web管理端 |

### 3.2 角色特性
- **一个账号可同时拥有车主和车位主人双重身份**
- 用户首次使用默认为车主角色
- 发布车位后自动获得车位主人身份

### 3.3 注册登录方式
- 微信授权登录（小程序）
- 手机号 + 短信验证码
- 实名认证：身份证验证（发布车位时必须）

---

## 四、功能模块详细设计

### 4.1 功能优先级总览

| 优先级 | 模块 | 说明 |
|-------|------|------|
| P0 (最高) | 用户注册/登录 | 基础功能，必须首先完成 |
| P0 | 车位发布与管理 | 核心业务功能 |
| P1 | 车位搜索与地图展示 | 核心用户体验 |
| P1 | 订单结算与账单管理 | 业务闭环必需 |
| P2 | 预约下单支付 | 模拟支付流程 |
| P3 | 使用核销 | 验证码核销 |

---

### 4.2 用户模块 (P0)

#### 4.2.1 功能列表

| 功能 | 描述 |
|-----|------|
| 微信登录 | 微信授权快捷登录 |
| 手机号登录 | 手机号+验证码登录 |
| 实名认证 | 身份证号+姓名验证 |
| 个人信息管理 | 头像、昵称、手机号修改 |
| 角色切换 | 车主/车位主人视图切换 |

#### 4.2.2 信用体系

| 规则项 | 数值 |
|-------|------|
| 初始信用分 | 100分 |
| 超时未使用扣分 | -5分/次 |
| 禁止预约阈值 | 60分以下 |
| 信用恢复方式 | 按时按规则完成停车 +2分/次 |
| 信用分上限 | 100分 |

#### 4.2.3 数据模型

```sql
-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(64) UNIQUE COMMENT '微信openid',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    real_name VARCHAR(50) COMMENT '真实姓名',
    id_card VARCHAR(18) COMMENT '身份证号',
    is_verified TINYINT DEFAULT 0 COMMENT '是否实名认证 0-否 1-是',
    credit_score INT DEFAULT 100 COMMENT '信用分',
    is_owner TINYINT DEFAULT 0 COMMENT '是否为车位主人 0-否 1-是',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

### 4.3 车位模块 (P0)

#### 4.3.1 功能列表

| 功能 | 角色 | 描述 |
|-----|------|------|
| 发布车位 | 车位主人 | 填写车位信息、上传照片、设置位置 |
| 编辑车位 | 车位主人 | 修改车位信息 |
| 上下架车位 | 车位主人 | 控制车位是否可被搜索预约 |
| 删除车位 | 车位主人 | 删除已发布车位 |
| 时段管理 | 车位主人 | 设置可用时段规则 |
| 车位审核 | 管理端 | 审核车位发布申请 |

#### 4.3.2 车位类型（V1版本）
- 小区私家车位

#### 4.3.3 计费规则（V1版本）
- **仅支持按小时计费**
- 车位主人自定义单价（元/小时）

#### 4.3.4 时段设置模式

**混合模式支持：**

1. **长期规则**：
   - 每周重复（如：每周一至周五 9:00-18:00 可用）
   
2. **临时调整**：
   - 单独设置某天不可用
   - 单独设置某天特定时段可用

#### 4.3.5 数据模型

```sql
-- 车位表
CREATE TABLE parking_space (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL COMMENT '车位主人ID',
    title VARCHAR(100) NOT NULL COMMENT '车位标题',
    description TEXT COMMENT '车位描述',
    community_name VARCHAR(100) NOT NULL COMMENT '小区名称',
    address VARCHAR(255) NOT NULL COMMENT '详细地址',
    longitude DECIMAL(10,7) NOT NULL COMMENT '经度',
    latitude DECIMAL(10,7) NOT NULL COMMENT '纬度',
    space_number VARCHAR(50) COMMENT '车位编号',
    price_per_hour DECIMAL(10,2) NOT NULL COMMENT '每小时价格',
    images VARCHAR(1000) COMMENT '车位图片URLs，JSON数组',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已上架 2-已下架 3-审核拒绝',
    reject_reason VARCHAR(255) COMMENT '审核拒绝原因',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES user(id)
);

-- 车位可用时段规则表
CREATE TABLE parking_space_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    space_id BIGINT NOT NULL COMMENT '车位ID',
    rule_type TINYINT NOT NULL COMMENT '规则类型 1-每周重复 2-单次设置',
    day_of_week VARCHAR(20) COMMENT '周几 1-7，多个用逗号分隔',
    specific_date DATE COMMENT '特定日期（单次设置时使用）',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    is_available TINYINT DEFAULT 1 COMMENT '是否可用 0-不可用 1-可用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (space_id) REFERENCES parking_space(id)
);
```

---

### 4.4 搜索与地图模块 (P1)

#### 4.4.1 功能列表

| 功能 | 描述 |
|-----|------|
| 地图展示 | 地图上显示附近可用车位标记 |
| 位置搜索 | 按地点名称/地址搜索 |
| 附近车位 | 基于当前位置显示附近车位 |
| 筛选条件 | 价格范围、距离范围筛选 |
| 车位详情 | 查看车位详细信息、图片、可用时段 |
| 导航集成 | 预约成功后唤起百度地图导航 |

#### 4.4.2 智能推荐

基于以下因素进行车位推荐：
- 用户当前位置
- 历史预约记录
- 收藏的车位
- 价格偏好

---

### 4.5 预约模块 (P2)

#### 4.5.1 功能列表

| 功能 | 描述 |
|-----|------|
| 时段可视化 | 日历视图展示车位可用/已预约时段 |
| 创建预约 | 选择时段、确认价格、提交预约 |
| 取消预约 | 在规定时间内取消预约 |
| 预约列表 | 查看我的预约记录 |
| 预约详情 | 查看预约详细信息、核销码 |

#### 4.5.2 预约规则

| 规则项 | 数值 |
|-------|------|
| 最短预约时长 | 0.5小时（30分钟） |
| 最长预约时长 | 12小时 |
| 最大提前预约时间 | 3天 |
| 免费取消时限 | 开始前1小时 |
| 取消政策 | 开始前1小时内取消不退款 |

#### 4.5.3 预约状态流转

```
待支付 → 已支付/待使用 → 使用中 → 已完成
   ↓           ↓            ↓
 已取消      已取消        异常结束
              ↓
           已超时（未核销）
```

#### 4.5.4 数据模型

```sql
-- 预约订单表
CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '预约用户ID',
    space_id BIGINT NOT NULL COMMENT '车位ID',
    owner_id BIGINT NOT NULL COMMENT '车位主人ID',
    start_time DATETIME NOT NULL COMMENT '预约开始时间',
    end_time DATETIME NOT NULL COMMENT '预约结束时间',
    duration DECIMAL(4,2) NOT NULL COMMENT '时长（小时）',
    price_per_hour DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待支付 1-待使用 2-使用中 3-已完成 4-已取消 5-已超时',
    verify_code VARCHAR(6) COMMENT '核销验证码',
    verified_at DATETIME COMMENT '核销时间',
    cancelled_at DATETIME COMMENT '取消时间',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (space_id) REFERENCES parking_space(id),
    FOREIGN KEY (owner_id) REFERENCES user(id)
);
```

---

### 4.6 支付模块 (P2)

#### 4.6.1 V1版本方案
**模拟支付流程**（不接入真实支付）

#### 4.6.2 支付流程

```
1. 用户确认预约信息
2. 点击"确认支付"
3. 显示模拟支付页面
4. 点击"确认支付"按钮
5. 系统模拟支付成功
6. 跳转支付成功页面
```

#### 4.6.3 V1暂不抽成
平台暂不从交易中抽取费用

---

### 4.7 核销模块 (P3)

#### 4.7.1 核销方式
**验证码核销**（6位数字）

#### 4.7.2 核销流程

```
1. 用户到达车位
2. 打开预约详情，查看6位验证码
3. 将验证码告知车位主人
4. 车位主人在小程序中输入验证码
5. 系统验证通过，订单状态变为"使用中"
6. 开始计时
```

#### 4.7.3 使用结束

```
1. 用户使用完毕，离开车位
2. 车位主人确认车辆已离开
3. 点击"确认结束"
4. 订单状态变为"已完成"
5. 费用结算
```

---

### 4.8 订单与结算模块 (P1)

#### 4.8.1 功能列表

| 功能 | 角色 | 描述 |
|-----|------|------|
| 订单列表 | 车主 | 查看我的预约订单 |
| 订单详情 | 车主 | 查看订单详细信息 |
| 收入明细 | 车位主人 | 查看车位出租收入 |
| 账单统计 | 车位主人 | 按日/周/月统计收入 |
| 提现申请 | 车位主人 | 申请提现余额（V1模拟） |

#### 4.8.2 数据模型

```sql
-- 账单流水表
CREATE TABLE transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    reservation_id BIGINT COMMENT '关联预约ID',
    type TINYINT NOT NULL COMMENT '类型 1-支出 2-收入 3-提现',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    balance DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    description VARCHAR(255) COMMENT '描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (reservation_id) REFERENCES reservation(id)
);

-- 用户钱包表
CREATE TABLE wallet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL COMMENT '用户ID',
    balance DECIMAL(10,2) DEFAULT 0 COMMENT '可用余额',
    frozen_amount DECIMAL(10,2) DEFAULT 0 COMMENT '冻结金额',
    total_income DECIMAL(10,2) DEFAULT 0 COMMENT '累计收入',
    total_withdraw DECIMAL(10,2) DEFAULT 0 COMMENT '累计提现',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);
```

---

## 五、Web管理端功能

### 5.1 角色权限

| 功能模块 | 超级管理员 | 运营人员 |
|---------|-----------|---------|
| 用户管理 | ✓ | ✓（仅查看） |
| 车位审核 | ✓ | ✓ |
| 订单管理 | ✓ | ✓ |
| 数据统计 | ✓ | ✓ |
| 系统配置 | ✓ | ✗ |
| 管理员管理 | ✓ | ✗ |

### 5.2 功能详细

#### 5.2.1 用户管理
- 用户列表（分页、搜索）
- 用户详情查看
- 用户封禁/解封
- 信用分调整

#### 5.2.2 车位审核
- 待审核车位列表
- 车位详情审核
- 通过/拒绝操作
- 拒绝原因填写

#### 5.2.3 订单管理
- 订单列表（多条件筛选）
- 订单详情查看
- 异常订单处理
- 退款操作

#### 5.2.4 数据统计
- 用户统计（新增、活跃）
- 车位统计（发布数、审核通过率）
- 订单统计（订单量、成交额）
- 收入趋势图表

#### 5.2.5 系统配置
- 信用规则配置
- 预约规则配置
- 系统参数设置

### 5.3 数据模型

```sql
-- 管理员表
CREATE TABLE admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role TINYINT NOT NULL COMMENT '角色 1-超级管理员 2-运营人员',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    last_login_at DATETIME COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 六、特色功能设计

### 6.1 智能推荐

#### 推荐算法因素
| 因素 | 权重 |
|-----|------|
| 距离（越近越优先） | 40% |
| 价格匹配度 | 25% |
| 历史预约偏好 | 20% |
| 车位评分 | 15% |

### 6.2 时段可视化

- 日历视图展示
- 可用时段绿色显示
- 已预约时段灰色显示
- 支持左右滑动切换日期

### 6.3 导航集成

- 预约成功后显示"导航前往"按钮
- 调用百度地图SDK唤起导航
- 支持步行/驾车导航模式

### 6.4 信用体系

```
信用分变动规则：
┌────────────────────────────┬────────┐
│ 行为                       │ 分值   │
├────────────────────────────┼────────┤
│ 初始信用分                 │ 100    │
│ 按时完成订单               │ +2     │
│ 超时未核销                 │ -5     │
│ 取消订单（正常时间内）      │ 0      │
│ 临时取消（1小时内）         │ -3     │
│ 被投诉且核实               │ -10    │
│ 信用分上限                 │ 100    │
│ 禁止预约阈值               │ 60     │
└────────────────────────────┴────────┘
```

---

## 七、接口设计概要

### 7.1 接口规范

- RESTful API 设计
- 统一响应格式
- JWT Token 认证
- 接口版本控制 `/api/v1/`

### 7.2 响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": {},
    "timestamp": 1710403200000
}
```

### 7.3 核心接口列表

#### 用户模块
| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/v1/auth/wx-login | 微信登录 |
| POST | /api/v1/auth/phone-login | 手机号登录 |
| POST | /api/v1/user/verify | 实名认证 |
| GET | /api/v1/user/profile | 获取用户信息 |
| PUT | /api/v1/user/profile | 更新用户信息 |

#### 车位模块
| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/v1/parking-space | 发布车位 |
| GET | /api/v1/parking-space | 获取车位列表 |
| GET | /api/v1/parking-space/{id} | 获取车位详情 |
| PUT | /api/v1/parking-space/{id} | 更新车位信息 |
| PUT | /api/v1/parking-space/{id}/status | 上下架车位 |
| DELETE | /api/v1/parking-space/{id} | 删除车位 |
| GET | /api/v1/parking-space/nearby | 获取附近车位 |

#### 预约模块
| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/v1/reservation | 创建预约 |
| GET | /api/v1/reservation | 获取预约列表 |
| GET | /api/v1/reservation/{id} | 获取预约详情 |
| PUT | /api/v1/reservation/{id}/cancel | 取消预约 |
| POST | /api/v1/reservation/{id}/verify | 核销验证 |
| PUT | /api/v1/reservation/{id}/complete | 完成订单 |

#### 钱包模块
| 方法 | 路径 | 描述 |
|-----|------|------|
| GET | /api/v1/wallet | 获取钱包信息 |
| GET | /api/v1/wallet/transactions | 获取交易记录 |
| POST | /api/v1/wallet/withdraw | 申请提现 |

---

## 八、开发计划

### 8.1 里程碑规划

```
Phase 1: 基础框架搭建（1周）
├── 后端项目初始化
├── 数据库设计与创建
├── 基础架构搭建
└── 统一响应、异常处理

Phase 2: 用户模块（1周）
├── 微信登录
├── 手机号登录
├── 实名认证
└── 个人信息管理

Phase 3: 车位模块（1.5周）
├── 车位CRUD
├── 时段规则管理
├── 地图集成
└── 搜索功能

Phase 4: 预约与订单（1.5周）
├── 预约流程
├── 模拟支付
├── 核销功能
└── 订单管理

Phase 5: 管理端开发（1周）
├── 管理端基础框架
├── 用户管理
├── 车位审核
├── 订单管理
└── 数据统计

Phase 6: 优化与测试（1周）
├── 智能推荐
├── 信用体系
├── 性能优化
└── 测试与修复
```

### 8.2 总工期
预计 **7-8周** 完成V1版本

---

## 九、约束与假设

### 9.1 约束条件
- V1版本仅支持小区私家车位
- V1版本仅支持按小时计费
- V1版本使用模拟支付，不接入真实支付
- V1版本平台不抽成

### 9.2 假设条件
- 用户已安装微信且能正常使用小程序
- 用户手机具备GPS定位功能
- 用户同意授权位置信息

---

## 十、名词解释

| 名词 | 解释 |
|-----|------|
| 车主 | 需要寻找停车位的用户 |
| 车位主人 | 拥有私家车位并愿意共享出租的用户 |
| 核销 | 用户到达车位后，通过验证码确认开始使用 |
| 信用分 | 用户行为评分，影响预约资格 |

---

## 附录：完整数据库ER图

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│    user      │       │  parking_space   │       │  reservation │
├──────────────┤       ├──────────────────┤       ├──────────────┤
│ id           │──┐    │ id               │──┐    │ id           │
│ openid       │  │    │ owner_id      ◄──┼──┤    │ order_no     │
│ phone        │  │    │ title            │  │    │ user_id   ◄──┼──┐
│ nickname     │  │    │ community_name   │  │    │ space_id  ◄──┼──┤
│ credit_score │  │    │ address          │  │    │ owner_id  ◄──┼──┤
│ is_owner     │  │    │ longitude        │  │    │ start_time   │  │
│ ...          │  │    │ latitude         │  │    │ end_time     │  │
└──────────────┘  │    │ price_per_hour   │  │    │ total_amount │  │
                  │    │ status           │  │    │ status       │  │
                  │    │ ...              │  │    │ verify_code  │  │
                  │    └──────────────────┘  │    │ ...          │  │
                  │              │           │    └──────────────┘  │
                  │              ▼           │           │          │
                  │    ┌──────────────────┐  │           │          │
                  │    │ parking_space_rule│  │           │          │
                  │    ├──────────────────┤  │           │          │
                  │    │ id               │  │           │          │
                  │    │ space_id      ◄──┼──┘           │          │
                  │    │ rule_type        │              │          │
                  │    │ day_of_week      │              │          │
                  │    │ start_time       │              │          │
                  │    │ end_time         │              │          │
                  │    │ ...              │              │          │
                  │    └──────────────────┘              │          │
                  │                                      │          │
                  │    ┌──────────────┐    ┌─────────────┼──────────┘
                  │    │   wallet     │    │             │
                  │    ├──────────────┤    │             │
                  │    │ id           │    │    ┌────────┘
                  └──► │ user_id      │    │    │
                       │ balance      │    │    │
                       │ ...          │    │    │
                       └──────────────┘    │    │
                                          │    │
                       ┌──────────────┐    │    │
                       │ transaction  │ ◄──┘    │
                       ├──────────────┤         │
                       │ id           │         │
                       │ user_id   ◄──┼─────────┘
                       │ reservation_id│
                       │ type         │
                       │ amount       │
                       │ ...          │
                       └──────────────┘

                       ┌──────────────┐
                       │    admin     │
                       ├──────────────┤
                       │ id           │
                       │ username     │
                       │ password     │
                       │ role         │
                       │ ...          │
                       └──────────────┘
```

---

**文档版本历史**

| 版本 | 日期 | 修改内容 | 作者 |
|-----|------|---------|-----|
| V1.0 | 2026-03-14 | 初始版本 | - |