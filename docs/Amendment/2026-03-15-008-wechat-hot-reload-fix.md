# 前端热重载报错修复

> 日期：2026-03-15
> 范围：登录跳转与 dashboard 样式兼容

## 变更概述
1. login.vue 登录成功跳转改为 `/pages/index/index`，避免 switchTab 跳转到非 tab 页导致运行时报错。
2. dashboard.vue 状态样式类名改为英文，移除 WXSS 不兼容的中文类名转义。

## 影响
- 修复微信开发者工具运行时报错与 WXSS 编译错误。
