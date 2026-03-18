<template>
  <view class="received-page">
    <view class="tab-bar">
      <view
        class="tab-item"
        v-for="tab in tabs"
        :key="tab.value"
        :class="{ active: currentTab === tab.value }"
        @click="switchTab(tab.value)"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <scroll-view
      class="order-list"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="orders.length === 0 && !loading" class="empty-state">
        <text>暂无订单</text>
      </view>

      <view class="order-item" v-for="order in orders" :key="order.id">
        <view class="order-header" @click="goDetail(order.id)">
          <text class="order-no">订单号：{{ order.orderNo }}</text>
          <text class="order-status" :class="statusClass(order.status)">{{ statusText(order.status) }}</text>
        </view>

        <view class="order-body" @click="goDetail(order.id)">
          <view class="space-info">
            <image :src="order.spaceImage || '/static/images/default-space.png'" class="space-image" mode="aspectFill"></image>
            <view class="space-detail">
              <text class="space-address">{{ order.spaceAddress || '停车位' }}</text>
              <text class="space-time">{{ formatTime(order.startTime) }} - {{ formatTime(order.endTime) }}</text>
            </view>
          </view>
          <view class="order-amount">
            <text class="amount-label">实付</text>
            <text class="amount-value">¥{{ order.actualAmount || order.amount }}</text>
          </view>
        </view>

        <view class="order-footer">
          <text class="create-time">{{ formatDateTime(order.createTime) }}</text>
          <view class="order-actions">
            <button
              class="action-btn verify"
              v-if="order.status === 1"
              @click.stop="goVerify(order)"
            >核销</button>
            <button
              class="action-btn complete"
              v-if="order.status === 2"
              @click.stop="completeOrder(order)"
            >完成</button>
            <button
              class="action-btn detail"
              @click.stop="goDetail(order.id)"
            >详情</button>
          </view>
        </view>
      </view>

      <view class="load-more" v-if="orders.length > 0">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getReceivedReservations, completeReservation } from '../../api/reservation.js'

export default {
  data() {
    return {
      currentTab: 'all',
      tabs: [
        { label: '全部', value: 'all' },
        { label: '待使用', value: 1 },
        { label: '使用中', value: 2 },
        { label: '已完成', value: 3 },
        { label: '已取消', value: 4 }
      ],
      orders: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      refreshing: false,
      noMore: false
    }
  },
  onShow() {
    this.refreshList()
  },
  methods: {
    switchTab(value) {
      if (this.currentTab === value) return
      this.currentTab = value
      this.refreshList()
    },
    async loadOrders(append = false) {
      if (this.loading) return
      this.loading = true

      try {
        const status = this.currentTab === 'all' ? null : this.currentTab
        const res = await getReceivedReservations(status, this.pageNum, this.pageSize)
        if (res.code === 200) {
          const list = res.data.records || res.data.list || res.data || []
          this.orders = append ? [...this.orders, ...list] : list
          const total = res.data.total || list.length
          this.noMore = this.orders.length >= total
        }
      } catch (e) {
        console.error('加载订单失败:', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
        this.refreshing = false
      }
    },
    refreshList() {
      this.pageNum = 1
      this.noMore = false
      this.loadOrders(false)
    },
    loadMore() {
      if (this.loading || this.noMore) return
      this.pageNum++
      this.loadOrders(true)
    },
    onRefresh() {
      this.refreshing = true
      this.refreshList()
    },
    statusText(status) {
      const map = { 0: '待支付', 1: '待使用', 2: '使用中', 3: '已完成', 4: '已取消', 5: '已超时' }
      return map[status] || '未知'
    },
    statusClass(status) {
      const map = { 0: 'pending', 1: 'pending', 2: 'using', 3: 'completed', 4: 'cancelled', 5: 'cancelled' }
      return map[status] || ''
    },
    formatTime(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      return `${hour}:${minute}`
    },
    formatDateTime(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const month = date.getMonth() + 1
      const day = date.getDate()
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      return `${month}月${day}日 ${hour}:${minute}`
    },
    goVerify(order) {
      uni.navigateTo({ url: `/pages/reservation/verify?id=${order.id}&orderNo=${order.orderNo}` })
    },
    goDetail(id) {
      uni.navigateTo({ url: `/pages/reservation/detail?id=${id}` })
    },
    completeOrder(order) {
      uni.showModal({
        title: '完成订单',
        content: '确认已完成订单？',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await completeReservation(order.id)
            if (result.code === 200) {
              uni.showToast({ title: '订单已完成', icon: 'success' })
              this.refreshList()
            } else {
              uni.showToast({ title: result.message || '操作失败', icon: 'none' })
            }
          } catch (e) {
            uni.showToast({ title: '操作失败', icon: 'none' })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.received-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.tab-bar {
  display: flex;
  background: #fff;
  padding: 0 20rpx;
  border-bottom: 1rpx solid #eee;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 30rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #007bff;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background: #007bff;
  border-radius: 2rpx;
}

.order-list {
  flex: 1;
  padding: 20rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
  color: #999;
  font-size: 28rpx;
}

.order-item {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.order-no {
  font-size: 24rpx;
  color: #999;
}

.order-status {
  font-size: 26rpx;
  font-weight: 500;
}

.order-status.pending { color: #ff9800; }
.order-status.using { color: #2196f3; }
.order-status.completed { color: #4caf50; }
.order-status.cancelled { color: #f44336; }

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
}

.space-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.space-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  background: #f0f0f0;
}

.space-detail {
  margin-left: 20rpx;
  flex: 1;
}

.space-address {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.space-time {
  font-size: 24rpx;
  color: #999;
}

.order-amount {
  text-align: right;
}

.amount-label {
  font-size: 24rpx;
  color: #999;
}

.amount-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #ff6b6b;
  margin-left: 8rpx;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #fafafa;
}

.create-time {
  font-size: 24rpx;
  color: #999;
}

.order-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  padding: 0 24rpx;
  height: 56rpx;
  line-height: 56rpx;
  font-size: 24rpx;
  border-radius: 28rpx;
  border: none;
}

.action-btn.verify { background: #007bff; color: #fff; }
.action-btn.complete { background: #4caf50; color: #fff; }
.action-btn.detail { background: #f5f5f5; color: #666; }

.load-more {
  text-align: center;
  padding: 30rpx 0;
  font-size: 26rpx;
  color: #999;
}
</style>
