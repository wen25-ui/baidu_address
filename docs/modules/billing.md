# 计费管理模块

> 最后更新: 2026-03-05

## 模块说明

计费管理模块负责导航服务的计费记录管理和费用计算。

## 文件清单

| 文件 | 说明 |
| ---- | ---- |
| `controller/BillingController.java` | 计费 REST API 控制器 |
| `service/BillingService.java` | 计费服务接口 |
| `service/impl/BillingServiceImpl.java` | 计费服务实现 |
| `model/entity/BillingRecord.java` | 计费记录实体类 |
| `model/dto/BillingDTO.java` | 计费数据传输对象 |
| `model/vo/RevenueVO.java` | 营收视图对象 |
| `repository/BillingRepository.java` | 计费数据仓库 |
| `utils/BillingUtil.java` | 计费工具类 |
| `common/BillingConstants.java` | 计费常量 |
| `resources/mapper/BillingMapper.xml` | MyBatis 映射文件 |

## API 接口

| 方法 | 路径 | 说明 |
| ---- | ---- | ---- |
| POST | `/api/billing/create` | 创建计费记录 |
| GET | `/api/billing/list` | 获取所有计费记录 |
| GET | `/api/billing/{id}` | 根据 ID 获取计费记录 |
| DELETE | `/api/billing/{id}` | 删除计费记录 |

## 计费规则

| 参数 | 值 | 说明 |
| ---- | ---- | ---- |
| BASE_RATE | 6.0 | 基础费率（元/小时） |
| MAX_RATE | 10.0 | 最高费率（元/小时） |
| PLATFORM_COMMISSION_RATE | 0.2 | 平台佣金比例（20%） |

## 实体字段 (BillingRecord)

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| id | Long | 主键 |
| userId | Long | 用户 ID |
| orderId | Long | 订单 ID |
| startTime | LocalDateTime | 计费开始时间 |
| endTime | LocalDateTime | 计费结束时间 |
| amount | Double | 计费金额 |
| platformFee | Double | 平台佣金 |

## 变更记录

### 2026-03-05

- 修复 `BillingService` 接口：重新定义为 CRUD 方法（`createBillingRecord`/`getAllBillingRecords` 等）
- 修复 `BillingController`：与新的 `BillingService` 接口对齐
- 修复 `BillingUtil`：添加 `convertToBillingRecord()` 和 `updateBillingRecord()` 方法
- 修正 `BillingUtil` 中 `BigDecimal` → `Double` 类型转换
