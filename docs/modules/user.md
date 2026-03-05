# 用户管理模块

> 最后更新: 2026-03-05

## 模块说明

用户管理模块负责平台用户的注册、登录、信息查询和管理。

## 文件清单

| 文件 | 说明 |
| ---- | ---- |
| `controller/UserController.java` | 用户 REST API 控制器 |
| `service/UserService.java` | 用户服务接口 |
| `service/impl/UserServiceImpl.java` | 用户服务实现 |
| `model/entity/User.java` | 用户实体类 |
| `model/dto/UserDTO.java` | 用户数据传输对象 |
| `repository/UserRepository.java` | 用户数据仓库 |
| `resources/mapper/UserMapper.xml` | MyBatis 映射文件 |

## API 接口

| 方法 | 路径 | 说明 |
| ---- | ---- | ---- |
| GET | `/api/users` | 获取所有用户 |
| GET | `/api/users/{id}` | 根据 ID 获取用户 |
| POST | `/api/users` | 创建用户 |
| PUT | `/api/users/{id}` | 更新用户 |
| DELETE | `/api/users/{id}` | 删除用户 |

## 实体字段

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| id | Long | 主键 |
| username | String | 用户名（唯一） |
| password | String | 密码 |
| email | String | 邮箱 |
| phone | String | 手机号 |
| role | String | 角色 |

## 变更记录

### 2026-03-05

- 创建 `UserDTO.java`（之前缺失）
- 修复 `UserService` 接口：`register()` → `createUser()`，`updateUser` 返回 `User`
- 修复 `UserServiceImpl`：添加 `login()` 方法实现
- 修复 `UserController`：`createUser` 和 `updateUser` 与服务层对齐
