<template>
  <view class="revenue-page">
    <view class="card">
      <text class="card-title">收入概览</text>
      <view class="stats-grid">
        <view class="stat-item highlight">
          <text class="stat-value">{{ totalRevenue }}</text>
          <text class="stat-label">订单总额(元)</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ platformRevenue }}</text>
          <text class="stat-label">平台收入(元)</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ totalOrders }}</text>
          <text class="stat-label">总订单数</text>
        </view>
      </view>
    </view>
    <view class="card">
      <text class="card-title">统计说明</text>
      <view class="empty-state">
        <text>当前展示为平台汇总统计，明细可在管理端查看</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getPlatformStats } from '../../api/admin.js'

export default {
  data() {
    return {
      totalRevenue: 0,
      platformRevenue: 0,
      totalOrders: 0
    }
  },
  onShow() {
    this.loadRevenue()
  },
  methods: {
    async loadRevenue() {
      try {
        const res = await getPlatformStats()
        const data = res.data || {}
        this.totalRevenue = data.totalRevenue || 0
        this.platformRevenue = data.platformRevenue || 0
        this.totalOrders = data.totalReservations || 0
      } catch (e) {
        console.error('获取收入数据失败:', e)
      }
    }
  }
}
</script>

<style scoped>
.revenue-page { padding: 20rpx; }
.stats-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-top: 10rpx;
}
.stat-item {
  width: 30%;
  text-align: center;
  padding: 24rpx 0;
  background: #f8f9fa;
  border-radius: 12rpx;
}
.stat-item.highlight {
  background: linear-gradient(135deg, #007bff, #0056b3);
}
.stat-item.highlight .stat-value,
.stat-item.highlight .stat-label { color: #fff; }
.stat-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #007bff;
  display: block;
}
.stat-label {
  font-size: 22rpx;
  color: #666;
  margin-top: 6rpx;
  display: block;
}
</style>
