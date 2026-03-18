<template>
  <view class="dashboard-page">
    <view class="card header-card">
      <view class="title-row">
        <text class="title">管理控制台</text>
        <button class="ghost-btn" @click="refreshData" :loading="loading">刷新</button>
      </view>
      <text class="subtitle">平台运营核心指标</text>
    </view>

    <view class="card">
      <view class="grid">
        <view class="stat-item">
          <text class="stat-value">{{ stats.totalUsers }}</text>
          <text class="stat-label">总用户</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.activeUsers }}</text>
          <text class="stat-label">活跃用户</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.totalParkingSpaces }}</text>
          <text class="stat-label">总车位</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.onlineParkingSpaces }}</text>
          <text class="stat-label">上架车位</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.totalReservations }}</text>
          <text class="stat-label">订单总数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.completedReservations }}</text>
          <text class="stat-label">完成订单</text>
        </view>
      </view>
    </view>

    <view class="card">
      <text class="section-title">收入概览</text>
      <view class="income-row">
        <text class="income-label">订单总额</text>
        <text class="income-value">¥{{ money(stats.totalRevenue) }}</text>
      </view>
      <view class="income-row">
        <text class="income-label">平台抽成</text>
        <text class="income-value highlight">¥{{ money(stats.platformRevenue) }}</text>
      </view>
      <view class="income-row">
        <text class="income-label">平均客单价</text>
        <text class="income-value">¥{{ money(income.avgOrderAmount) }}</text>
      </view>
    </view>

    <view class="card">
      <text class="section-title">快捷入口</text>
      <view class="entry-list">
        <view class="entry-item" @click="goTo('/pages/user/user')">
          <text class="entry-name">用户管理</text>
          <text class="entry-arrow">></text>
        </view>
        <view class="entry-item" @click="goTo('/pages/navigation/navigation')">
          <text class="entry-name">车位审核</text>
          <text class="entry-arrow">></text>
        </view>
        <view class="entry-item" @click="goTo('/pages/order/order')">
          <text class="entry-name">订单管理</text>
          <text class="entry-arrow">></text>
        </view>
        <view class="entry-item" @click="goTo('/pages/billing/billing')">
          <text class="entry-name">计费统计</text>
          <text class="entry-arrow">></text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPlatformStats, getIncomeStats } from '../../api/admin.js'
import { getToken, getUserInfo } from '../../utils/auth.js'

const defaultStats = () => ({
  totalUsers: 0,
  activeUsers: 0,
  totalParkingSpaces: 0,
  onlineParkingSpaces: 0,
  totalReservations: 0,
  completedReservations: 0,
  totalRevenue: 0,
  platformRevenue: 0
})

const defaultIncome = () => ({
  totalAmount: 0,
  platformFee: 0,
  orderCount: 0,
  avgOrderAmount: 0
})

export default {
  data() {
    return {
      loading: false,
      stats: defaultStats(),
      income: defaultIncome()
    }
  },
  onShow() {
    if (!this.ensureAdmin()) return
    this.refreshData()
  },
  methods: {
    ensureAdmin() {
      const token = getToken()
      const user = getUserInfo()
      if (!token) {
        uni.reLaunch({ url: '/pages/login/login' })
        return false
      }
      /* #ifdef H5 */
      if (!user || !user.isAdmin) {
        uni.showToast({ title: '请使用管理员账号登录', icon: 'none' })
        uni.reLaunch({ url: '/pages/login/login' })
        return false
      }
      /* #endif */
      return true
    },
    async refreshData() {
      this.loading = true
      try {
        const [statsRes, incomeRes] = await Promise.all([
          getPlatformStats(),
          getIncomeStats()
        ])
        this.stats = { ...defaultStats(), ...(statsRes.data || {}) }
        this.income = { ...defaultIncome(), ...(incomeRes.data || {}) }
      } catch (error) {
        uni.showToast({ title: error?.message || '加载数据失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    money(value) {
      const num = Number(value || 0)
      return Number.isFinite(num) ? num.toFixed(2) : '0.00'
    },
    goTo(url) {
      uni.navigateTo({ url })
    }
  }
}
</script>

<style scoped>
.dashboard-page {
  padding: 20rpx;
}

.card {
  background: #fff;
  border-radius: 14rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.08);
}

.header-card {
  background: linear-gradient(135deg, #1f6fff 0%, #3f8cff 100%);
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  color: #fff;
  font-size: 34rpx;
  font-weight: 700;
}

.subtitle {
  display: block;
  margin-top: 12rpx;
  color: rgba(255, 255, 255, 0.86);
  font-size: 24rpx;
}

.ghost-btn {
  height: 56rpx;
  line-height: 56rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  border-radius: 28rpx;
  font-size: 24rpx;
  padding: 0 20rpx;
}

.section-title {
  display: block;
  font-size: 28rpx;
  color: #222;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.stat-item {
  width: calc(50% - 6rpx);
  background: #f7f9fc;
  border-radius: 10rpx;
  padding: 16rpx;
  box-sizing: border-box;
}

.stat-value {
  display: block;
  font-size: 34rpx;
  color: #1f6fff;
  font-weight: 700;
}

.stat-label {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  color: #5f6b7a;
}

.income-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #eef2f8;
}

.income-row:last-child {
  border-bottom: none;
}

.income-label {
  font-size: 24rpx;
  color: #5f6b7a;
}

.income-value {
  font-size: 28rpx;
  color: #1f2937;
  font-weight: 600;
}

.income-value.highlight {
  color: #1f6fff;
}

.entry-list {
  display: flex;
  flex-direction: column;
}

.entry-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 84rpx;
  border-bottom: 1rpx solid #eef2f8;
}

.entry-item:last-child {
  border-bottom: none;
}

.entry-name {
  font-size: 28rpx;
  color: #1f2937;
}

.entry-arrow {
  font-size: 30rpx;
  color: #a3acb9;
}
</style>
