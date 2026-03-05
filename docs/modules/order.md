# 订单管理模块

> 最后更新: 2026-03-05

## 模块说明

订单管理模块负责导航订单的创建、查询和删除。

## 文件清单

| 文件 | 说明 |
| ---- | ---- |
| `controller/OrderController.java` | 订单 REST API 控制器 |
| `service/OrderService.java` | 订单服务接口 |
| `service/impl/OrderServiceImpl.java` | 订单服务实现 |
| `model/entity/Order.java` | 订单实体类 |
| `model/dto/OrderDTO.java` | 订单数据传输对象 |
| `model/vo/OrderVO.java` | 订单视图对象 |
| `repository/OrderRepository.java` | 订单数据仓库 |
| `resources/mapper/OrderMapper.xml` | MyBatis 映射文件 |

## API 接口

| 方法 | 路径 | 说明 |
| ---- | ---- | ---- |
| GET | `/api/orders` | 获取所有订单 |
| GET | `/api/orders/{id}` | 根据 ID 获取订单 |
| POST | `/api/orders` | 创建订单 |
| DELETE | `/api/orders/{id}` | 删除订单 |

## 实体字段 (Order)

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| id | Long | 主键 |
| userId | Long | 用户 ID |
| startLocation | String | 起点 |
| endLocation | String | 终点 |
| startTime | LocalDateTime | 开始时间 |
| endTime | LocalDateTime | 结束时间 |
| totalCost | Double | 总费用 |
| status | String | 订单状态 |

## 变更记录

### 2026-03-05

- 修复 `OrderService` 接口：返回类型从 `OrderVO` 改为 `Order`，移除 `updateOrder`
- 修复 `OrderServiceImpl`：使用 `Order` 实体正确字段（移除不存在的 `setDuration`/`setPrice`）
- 修复 `OrderController`：与 `OrderService` 新接口对齐
