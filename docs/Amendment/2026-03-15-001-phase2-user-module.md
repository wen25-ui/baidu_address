# Phase 2: 用户模块开发进度

## 日期
2026-03-15

## 变更类型
功能开发

## 变更内容

### 1. UserService 接口 (service/UserService.java)
- `wxLogin(String code)` - 微信登录，返回用户信息
- `getById(Long id)` - 根据ID获取用户
- `getByOpenid(String openid)` - 根据openid获取用户
- `updateUser(User user)` - 更新用户信息
- `verifyRealName(Long userId, String realName, String idCard)` - 实名认证
- `updateCreditScore(Long userId, Integer delta, String reason)` - 更新信用分
- `checkCreditScore(Long userId)` - 检查信用分是否满足预约条件

### 2. UserServiceImpl 实现 (service/impl/UserServiceImpl.java)
- 微信登录实现：调用微信API获取openid，新用户自动创建账户和钱包
- 信用分管理：记录信用分变动到CreditRecord表
- 使用 @Transactional 保证事务一致性

### 3. UserController 控制器 (controller/UserController.java)
- POST `/api/v1/user/wx-login` - 微信登录
- GET `/api/v1/user/info` - 获取用户信息
- PUT `/api/v1/user/info` - 更新用户信息
- POST `/api/v1/user/verify` - 实名认证
- GET `/api/v1/user/credit-check` - 检查信用分

### 4. JwtAuthenticationFilter (config/JwtAuthenticationFilter.java)
- JWT token验证过滤器
- 白名单路径配置（无需认证）
- 从token中提取userId和role注入request属性

### 5. JwtUtils 更新 (utils/JwtUtils.java)
- 新增 `getUserIdFromToken(String token)` 方法
- 新增 `getRoleFromToken(String token)` 方法

### 6. SecurityConfig 更新 (config/SecurityConfig.java)
- 集成 JwtAuthenticationFilter
- 添加到 UsernamePasswordAuthenticationFilter 之前

### 7. WebConfig 修复 (config/WebConfig.java)
- 修复 CORS 配置：`allowedOrigins("*")` 改为 `allowedOriginPatterns("*")`
- 解决 allowCredentials(true) 与通配符 origin 冲突

## 测试验证
- ✅ 编译成功 (mvn clean compile)
- ✅ 打包成功 (mvn package)
- ✅ 应用启动成功 (端口8080)
- ✅ 健康检查接口正常 (`/api/public/health`)
- ✅ 用户登录接口可达 (`/api/v1/user/wx-login`)

## 待办事项
- [ ] 配置正确的数据库密码
- [ ] 配置微信小程序 appid 和 secret
- [ ] 添加更多业务逻辑单元测试

## 技术债务
- 解决 UserServiceImpl.java 文件编码问题（UTF-8 BOM）：使用 PowerShell 的 `[System.IO.File]::WriteAllText` 方法创建无 BOM 文件

## 下一步计划
- Phase 3: 车位模块开发
  - ParkingSpaceService 接口
  - 车位发布、查询、上下架功能
  - 车位时段价格管理
