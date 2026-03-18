<template>
  <view class="billing-page">
    <view class="card">
      <text class="card-title">计费统计</text>
      <view class="filters">
        <view class="date-field">
          <text class="field-label">开始日期</text>
          <picker mode="date" :value="filters.startDate" @change="onStartDateChange">
            <view class="date-input">{{ filters.startDate || '请选择' }}</view>
          </picker>
        </view>
        <view class="date-field">
          <text class="field-label">结束日期</text>
          <picker mode="date" :value="filters.endDate" @change="onEndDateChange">
            <view class="date-input">{{ filters.endDate || '请选择' }}</view>
          </picker>
        </view>
      </view>
      <view class="filter-actions">
        <button class="tiny-btn" :loading="loading" @click="fetchData">查询</button>
        <button class="tiny-btn plain" @click="resetFilters">重置</button>
      </view>
    </view>

    <view class="card">
      <text class="section-title">收入汇总</text>
      <view class="summary-list">
        <view class="summary-item">
          <text class="summary-label">订单总额</text>
          <text class="summary-value">¥{{ money(summary.totalAmount) }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">平台抽成</text>
          <text class="summary-value highlight">¥{{ money(summary.platformFee) }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">订单数量</text>
          <text class="summary-value">{{ summary.orderCount || 0 }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">平均客单价</text>
          <text class="summary-value">¥{{ money(summary.avgOrderAmount) }}</text>
        </view>
      </view>
    </view>

    <view class="card">
      <view class="recent-head">
        <text class="section-title">最近完成订单</text>
        <button class="tiny-btn" :loading="loadingOrders" @click="fetchCompletedOrders">刷新</button>
      </view>
      <view v-if="completedOrders.length === 0 && !loadingOrders" class="empty-state">
        <text>暂无已完成订单</text>
      </view>
      <view v-for="item in completedOrders" :key="item.id" class="record-item">
        <text class="record-no">{{ item.orderNo || ('订单#' + item.id) }}</text>
        <view class="record-row">
          <text>车位ID：{{ item.spaceId }}</text>
          <text>用户ID：{{ item.userId }}</text>
          <text>金额：¥{{ money(item.totalAmount) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getBillingSummary, getBillingRecords } from '../../api/billing.js'
import { getToken, getUserInfo } from '../../utils/auth.js'

const defaultSummary = () => ({
  totalAmount: 0,
  platformFee: 0,
  orderCount: 0,
  avgOrderAmount: 0
})

export default {
  data() {
    return {
      loading: false,
      loadingOrders: false,
      filters: {
        startDate: '',
        endDate: ''
      },
      summary: defaultSummary(),
      completedOrders: []
    }
  },
  onShow() {
    if (!this.ensureAdmin()) return
    this.fetchData()
    this.fetchCompletedOrders()
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
    onStartDateChange(event) {
      this.filters.startDate = event.detail.value || ''
    },
    onEndDateChange(event) {
      this.filters.endDate = event.detail.value || ''
    },
    resetFilters() {
      this.filters.startDate = ''
      this.filters.endDate = ''
      this.fetchData()
    },
    async fetchData() {
      this.loading = true
      try {
        const res = await getBillingSummary(this.filters.startDate, this.filters.endDate)
        this.summary = { ...defaultSummary(), ...(res.data || {}) }
      } catch (error) {
        uni.showToast({ title: error?.message || '获取统计失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async fetchCompletedOrders() {
      this.loadingOrders = true
      try {
        const res = await getBillingRecords({ pageNum: 1, pageSize: 12 })
        const page = res.data || {}
        this.completedOrders = page.records || []
      } catch (error) {
        uni.showToast({ title: error?.message || '获取订单失败', icon: 'none' })
      } finally {
        this.loadingOrders = false
      }
    },
    money(value) {
      const num = Number(value || 0)
      return Number.isFinite(num) ? num.toFixed(2) : '0.00'
    }
  }
}
</script>

<style scoped>
.billing-page {
  padding: 20rpx;
}

.card {
  background: #fff;
  border-radius: 14rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.08);
}

.card-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.section-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12rpx;
}

.filters {
  display: flex;
  gap: 12rpx;
}

.date-field {
  flex: 1;
}

.field-label {
  display: block;
  font-size: 22rpx;
  color: #667085;
  margin-bottom: 6rpx;
}

.date-input {
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 10rpx;
  background: #f5f7fb;
  padding: 0 14rpx;
  font-size: 24rpx;
  color: #344054;
}

.filter-actions {
  display: flex;
  gap: 10rpx;
  margin-top: 12rpx;
}

.tiny-btn {
  height: 52rpx;
  line-height: 52rpx;
  border-radius: 26rpx;
  background: #1f6fff;
  color: #fff;
  font-size: 22rpx;
  border: none;
  padding: 0 18rpx;
}

.tiny-btn.plain {
  background: #eef5ff;
  color: #1f6fff;
}

.summary-list {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 64rpx;
  border-bottom: 1rpx solid #edf0f5;
}

.summary-item:last-child {
  border-bottom: none;
}

.summary-label {
  font-size: 24rpx;
  color: #667085;
}

.summary-value {
  font-size: 28rpx;
  color: #1f2937;
  font-weight: 600;
}

.summary-value.highlight {
  color: #1f6fff;
}

.recent-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-item {
  border: 1rpx solid #edf0f5;
  border-radius: 10rpx;
  padding: 14rpx;
  margin-bottom: 10rpx;
}

.record-no {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: #1f2937;
}

.record-row {
  margin-top: 8rpx;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  font-size: 22rpx;
  color: #475467;
}
</style>
