# 导航服务模块

> 最后更新: 2026-03-05

## 模块说明

导航服务模块负责路线管理和百度地图 API 集成，提供地理编码、路线规划和距离计算等功能。

## 文件清单

| 文件 | 说明 |
| ---- | ---- |
| `controller/NavigationController.java` | 导航 REST API 控制器 |
| `service/NavigationService.java` | 导航服务接口 |
| `service/impl/NavigationServiceImpl.java` | 导航服务实现 |
| `model/entity/Route.java` | 路线实体类 |
| `model/dto/NavigationDTO.java` | 导航数据传输对象 |
| `repository/RouteRepository.java` | 路线数据仓库 |
| `config/BaiduMapConfig.java` | 百度地图配置类 |
| `utils/BaiduMapUtil.java` | 百度地图工具类 |

## API 接口

| 方法 | 路径 | 说明 |
| ---- | ---- | ---- |
| GET | `/api/navigation/routes` | 获取所有路线 |
| POST | `/api/navigation/save` | 保存路线 |
| DELETE | `/api/navigation/routes/{id}` | 删除路线 |

## 百度地图集成

### 工具类方法 (BaiduMapUtil)

| 方法 | 说明 |
| ---- | ---- |
| `getLocation(address)` | 地理编码 - 地址转坐标 |
| `getRoute(origin, destination)` | 驾车路线规划 |
| `getDistance(origin, destination)` | 驾车距离计算 |

### 配置项

```yaml
# application.yml 中配置
baidu:
  map:
    ak: YOUR_BAIDU_MAP_AK  # 部署时替换
```

> ⚠️ 百度地图 AK 需要在部署时配置，当前使用占位符。

## 实体字段 (Route)

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| id | Long | 主键 |
| startLocation | String | 起点 |
| endLocation | String | 终点 |
| distance | Double | 距离 |
| duration | Double | 时长 |
| createdAt | Date | 创建时间 |
| updatedAt | Date | 更新时间 |

## 变更记录

### 2026-03-05

- 修复 `BaiduMapConfig`：移除不存在的 `com.baidu.mapapi.SDKInitializer`（百度地图服务端通过 REST API 调用）
- 修复 `NavigationService` 接口：重新定义为 `getRoutes()`/`saveRoute()`/`deleteRoute()`
- 修复 `NavigationServiceImpl`：移除 `NavigationDTO` 中不存在的 `getDistance()`/`getDuration()` 调用
- 修复 `NavigationController`：与新的 `NavigationService` 接口对齐
- 添加 `Gson` 依赖到 `pom.xml`（`BaiduMapUtil` 使用 `JsonParser`）
