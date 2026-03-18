<template>
  <view class="list-page">
    <!-- 状态标签栏 -->
    <view class="tab-bar">
      <view 
        class="tab-item" 
        v-for="tab in tabs" 
        :key="tab.value"
        :class="{ active: currentTab === tab.value }"
        @click="switchTab(tab.value)"
      >
        <text>{{ tab.label }}</text>
        <view class="tab-badge" v-if="tab.count > 0">{{ tab.count }}</view>
      </view>
    </view>

    <!-- 订单列表 -->
    <scroll-view 
      class="order-list" 
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="orders.length === 0 && !loading" class="empty-state">
        <image src="/static/images/empty.png" class="empty-icon" mode="aspectFit"></image>
        <text class="empty-text">暂无订单</text>
      </view>

      <view class="order-item" v-for="order in orders" :key="order.id" @click="goDetail(order.id)">
        <view class="order-header">
          <text class="order-no">订单号：{{ order.orderNo }}</text>
          <text class="order-status" :class="'status-' + order.status">{{ getStatusText(order.status) }}</text>
        </view>
        
        <view class="order-body">
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
              class="action-btn cancel" 
              v-if="order.status === 0 || order.status === 1"
              @click.stop="cancelOrder(order)"
            >取消</button>
            <button 
              class="action-btn pay" 
              v-if="order.status === 0"
              @click.stop="payOrder(order)"
            >支付</button>
            <button 
              class="action-btn qrcode" 
              v-if="order.status === 1 || order.status === 2"
              @click.stop="showQrcode(order)"
            >二维码</button>
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
import { getUserReservations, payReservation, cancelReservation } from '../../api/reservation.js'

export default {
  data() {
    return {
      currentTab: 'all',
      tabs: [
        { label: '全部', value: 'all', count: 0 },
        { label: '待支付', value: 'pending', count: 0 },
        { label: '进行中', value: 'active', count: 0 },
        { label: '已完成', value: 'completed', count: 0 }
      ],
      orders: [],
      page: 1,
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
        // 根据tab确定status参数
        let status = null
        if (this.currentTab === 'pending') status = 0
        else if (this.currentTab === 'active') status = '1,2'
        else if (this.currentTab === 'completed') status = '3,4'

        const res = await getUserReservations({
          page: this.page,
          pageSize: this.pageSize,
          status
        })

        if (res.code === 200) {
          const list = res.data.records || res.data.list || res.data || []
          if (append) {
            this.orders = [...this.orders, ...list]
          } else {
            this.orders = list
          }
          
          const total = res.data.total || list.length
          this.noMore = this.orders.length >= total
          
          // 更新badge计数
          this.updateTabCounts()
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
      this.page = 1
      this.noMore = false
      this.loadOrders(false)
    },
    loadMore() {
      if (this.loading || this.noMore) return
      this.page++
      this.loadOrders(true)
    },
    onRefresh() {
      this.refreshing = true
      this.refreshList()
    },
    updateTabCounts() {
      // 统计各状态数量
      const counts = { pending: 0, active: 0, completed: 0 }
      this.orders.forEach(order => {
        if (order.status === 0) counts.pending++
        else if (order.status === 1 || order.status === 2) counts.active++
        else if (order.status === 3 || order.status === 4) counts.completed++
      })
      
      this.tabs[1].count = counts.pending
      this.tabs[2].count = counts.active
    },
    getStatusText(status) {
      const map = { 0: '待支付', 1: '待使用', 2: '使用中', 3: '已完成', 4: '已取消' }
      return map[status] || '未知'
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
    goDetail(id) {
      uni.navigateTo({
        url: `/pages/reservation/detail?id=${id}`
      })
    },
    async payOrder(order) {
      uni.showLoading({ title: '支付中...' })
      try {
        const res = await payReservation(order.id)
        if (res.code === 200) {
          uni.showToast({ title: '支付成功', icon: 'success' })
          this.refreshList()
        } else {
          uni.showToast({ title: res.message || '支付失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '支付失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    cancelOrder(order) {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消此订单吗？',
        success: async (result) => {
          if (result.confirm) {
            uni.showLoading({ title: '取消中...' })
            try {
              const res = await cancelReservation(order.id)
              if (res.code === 200) {
                uni.showToast({ title: '取消成功', icon: 'success' })
                this.refreshList()
              } else {
                uni.showToast({ title: res.message || '取消失败', icon: 'none' })
              }
            } catch (e) {
              uni.showToast({ title: '取消失败', icon: 'none' })
            } finally {
              uni.hideLoading()
            }
          }
        }
      })
    },
    showQrcode(order) {
      uni.navigateTo({
        url: `/pages/reservation/detail?id=${order.id}`
      })
    }
  }
}
</script>

<style scoped>
.list-page {
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

.tab-badge {
  position: absolute;
  top: 16rpx;
  right: 20rpx;
  min-width: 32rpx;
  height: 32rpx;
  line-height: 32rpx;
  text-align: center;
  background: #ff6b6b;
  color: #fff;
  font-size: 20rpx;
  border-radius: 16rpx;
  padding: 0 8rpx;
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
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  opacity: 0.5;
}

.empty-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #999;
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

.order-status.status-0 { color: #ff9800; }
.order-status.status-1 { color: #4caf50; }
.order-status.status-2 { color: #2196f3; }
.order-status.status-3 { color: #9e9e9e; }
.order-status.status-4 { color: #f44336; }

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

.action-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.action-btn.pay {
  background: #ff6b6b;
  color: #fff;
}

.action-btn.qrcode {
  background: #007bff;
  color: #fff;
}

.load-more {
  text-align: center;
  padding: 30rpx 0;
  font-size: 26rpx;
  color: #999;
}
</style>
