# 变更记录 - 2026-03-14-002 后端框架搭建完成

**时间**: 2026-03-14 15:00  
**类型**: 框架搭建  
**作者**: System

---

## 变更概述

完成后端项目基础框架搭建，包含实体类、Mapper、配置类、工具类等。

## 变更内容

### 1. pom.xml更新

- 升级 Spring Boot 2.7.5 → 3.2.0
- 移除 JPA，改用 MyBatis Plus 3.5.5
- 添加 JWT (jjwt 0.12.3)
- 添加 Hutool 5.8.24
- 更新 fastjson → fastjson2

### 2. 配置文件更新

- application.yml 配置数据库连接
- 配置 MyBatis Plus
- 配置百度地图AK
- 配置 JWT
- 配置业务规则参数

### 3. 实体类创建

- User (用户)
- ParkingSpace (车位)
- ParkingSpaceRule (时段规则)
- Reservation (预约订单)
- Wallet (钱包)
- Transaction (交易流水)
- Admin (管理员)
- UserFavorite (收藏)
- CreditRecord (信用记录)

### 4. Mapper创建

9个Mapper接口，继承BaseMapper

### 5. 配置类创建

- MybatisPlusConfig (分页、自动填充)
- BaiduMapProperties
- JwtProperties
- ParkingRulesProperties
- SecurityConfig (Spring Security 6.x)

### 6. 工具类创建

- JwtUtils (JWT生成解析)
- OrderNoUtils (订单号生成)

### 7. 公共类更新

- Result (统一响应)
- ResultCode (状态码枚举)
- BusinessException (业务异常)
- GlobalExceptionHandler (全局异常处理)

## 待执行

1. 执行数据库初始化SQL
2. mvn clean install 安装依赖
3. 启动项目验证
