# 交互报错修复

> 日期：2026-03-15
> 范围：页面跳转、资源缺失、用户列表渲染

## 变更概述
1. 新增页面：parking/nearby、parking/search、revenue/revenue 并加入 pages.json。
2. user.vue 用户列表读取 records 并过滤空值，避免渲染报错。
3. 新增 static/images 下的占位资源，修复 empty.png 与默认图片缺失。
4. revenue 页面改为读取 /api/admin/stats/platform。

## 影响
- 修复跳转到不存在页面导致的报错。
- 修复用户管理列表渲染报错。
- 修复静态资源 500 错误。
