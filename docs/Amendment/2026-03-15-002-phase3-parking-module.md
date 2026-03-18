# 变更记录：Phase 3 - 车位模块

## 变更编号
2026-03-15-002

## 变更日期
2026-03-15

## 变更类型
新增功能

## 变更描述
实现车位模块（Parking Space Module），包括车位发布、管理、搜索等功能。

## 影响范围
- 后端服务层
- 后端控制层
- RESTful API

## 详细变更内容

### 1. 新增文件

#### 1.1 Service 接口
- `service/ParkingSpaceService.java`
  - 车位发布 `publish()`
  - 车位详情 `getById()`
  - 车位更新 `update()`
  - 车位删除 `delete()`
  - 上架 `online()`
  - 下架 `offline()`
  - 附近搜索 `searchNearby()` - 基于Haversine公式
  - 关键词搜索 `searchByKeyword()`
  - 车主车位列表 `getByOwnerId()`
  - 统计信息 `getStats()`
  - 内部记录类型 `ParkingSpaceStats`

- `service/ParkingSpaceRuleService.java`
  - 添加时段规则 `addRule()`
  - 批量添加规则 `batchAddRules()`
  - 获取车位规则 `getByParkingSpaceId()`
  - 获取可用时段 `getAvailableRules()`
  - 检查时段可用性 `checkAvailable()`

#### 1.2 Service 实现
- `service/impl/ParkingSpaceServiceImpl.java`
  - 完整实现车位管理逻辑
  - 状态常量：PENDING(0), ONLINE(1), OFFLINE(2)
  - Haversine公式实现地理位置搜索
  - 事务支持

- `service/impl/ParkingSpaceRuleServiceImpl.java`
  - 完整实现时段规则逻辑
  - 支持按周重复和指定日期规则
  - 时段冲突检测

#### 1.3 Controller
- `controller/ParkingSpaceController.java`

### 2. API 端点

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| POST | `/api/v1/parking` | 发布车位 | 需要 |
| GET | `/api/v1/parking/{id}` | 获取车位详情 | 可选 |
| PUT | `/api/v1/parking/{id}` | 更新车位信息 | 需要 |
| DELETE | `/api/v1/parking/{id}` | 删除车位 | 需要 |
| POST | `/api/v1/parking/{id}/online` | 车位上架 | 需要 |
| POST | `/api/v1/parking/{id}/offline` | 车位下架 | 需要 |
| GET | `/api/v1/parking/nearby` | 搜索附近车位 | 可选 |
| GET | `/api/v1/parking/search` | 关键词搜索 | 可选 |
| GET | `/api/v1/parking/mine` | 我的车位列表 | 需要 |
| GET | `/api/v1/parking/stats` | 车位统计 | 需要 |
| POST | `/api/v1/parking/{spaceId}/rules` | 添加时段规则 | 需要 |
| GET | `/api/v1/parking/{spaceId}/rules` | 获取时段规则 | 可选 |
| GET | `/api/v1/parking/{spaceId}/available` | 查询可用时段 | 可选 |
| DELETE | `/api/v1/parking/rules/{ruleId}` | 删除时段规则 | 需要 |

### 3. 附近搜索算法

使用 Haversine 公式计算地球表面两点间距离：

```java
// 地球半径（公里）
private static final double EARTH_RADIUS = 6371.0;

// Haversine 公式
double dLat = Math.toRadians(lat2 - lat1);
double dLng = Math.toRadians(lng2 - lng1);
double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
           Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
           Math.sin(dLng/2) * Math.sin(dLng/2);
double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
double distance = EARTH_RADIUS * c;
```

### 4. 车位状态流转

```
发布 → PENDING(0) → 审核通过 → OFFLINE(2) → 上架 → ONLINE(1)
                                    ↑            |
                                    └── 下架 ────┘
```

## 测试情况
- ✅ Maven 编译通过
- ✅ 应用启动成功
- ✅ Tomcat 8080 端口正常监听

## 后续计划
- Phase 4: 预约与订单模块
- Phase 5: 计费与结算模块
- Phase 6: 导航模块

## 相关文档
- PRD: `docs/requirements/PRD.md`
- 模块设计: `docs/modules/`
- 项目状态: `docs/PROJECT_STATUS.md`

---
*变更人: Agent*
*审核人: -*
