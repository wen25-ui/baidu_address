# 前端API兼容性修复记录

> 日期：2026-03-15
> 范围：钱包与订单API兼容、个人信息页展示优化

## 变更概述
1. wallet.js 兼容传入对象参数（recharge、withdraw、getTransactions）。
2. reservation.js 补充 getUserReservations 兼容旧调用方式。
3. profile.vue 去除默认头像静态资源依赖，改为首字母占位。

## 影响
- 修复前端页面调用参数不一致导致的接口错误。
- 个人信息页在无头像时也能正常展示。
