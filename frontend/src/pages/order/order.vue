<template>
  <view class="order-page">
    <view class="card">
      <text class="card-title">订单管理</text>
      <view class="filters">
        <input
          class="filter-input"
          v-model="filters.orderNo"
          placeholder="输入订单号关键词"
          @confirm="handleSearch"
        />
        <picker :range="statusLabels" :value="statusIndex" @change="onStatusChange">
          <view class="filter-picker">{{ statusLabels[statusIndex] }}</view>
        </picker>
        <button class="search-btn" @click="handleSearch" :loading="loading">查询</button>
      </view>
    </view>

    <view class="card">
      <view class="summary-row">
        <text class="summary-text">共 {{ total }} 条订单</text>
        <button class="tiny-btn" @click="fetchOrders" :loading="loading">刷新</button>
      </view>

      <view v-if="orders.length === 0 && !loading" class="empty-state">
        <text>暂无订单数据</text>
      </view>

      <view v-for="item in orders" :key="item.id" class="order-item">
        <view class="order-main">
          <text class="order-no">{{ item.orderNo || ('订单#' + item.id) }}</text>
          <text class="status-tag" :class="statusClass(item.status)">{{ statusText(item.status) }}</text>
        </view>
        <view class="order-info">
          <text>用户ID：{{ item.userId }}</text>
          <text>车位ID：{{ item.spaceId }}</text>
          <text>金额：¥{{ money(item.totalAmount) }}</text>
          <text>时段：{{ formatTime(item.startTime) }} ~ {{ formatTime(item.endTime) }}</text>
        </view>
        <view class="actions">
          <button class="tiny-btn" @click="viewDetail(item.id)">详情</button>
        </view>
      </view>

      <view class="pager" v-if="total > filters.pageSize">
        <button class="tiny-btn" :disabled="filters.pageNum <= 1 || loading" @click="prevPage">上一页</button>
        <text class="pager-text">{{ filters.pageNum }} / {{ totalPages }}</text>
        <button class="tiny-btn" :disabled="filters.pageNum >= totalPages || loading" @click="nextPage">下一页</button>
      </view>
    </view>
  </view>
</template>

<script>
import { getOrderList, getOrderDetail } from '../../api/order.js'
import { getToken, getUserInfo } from '../../utils/auth.js'

const statusValues = ['', '0', '1', '2', '3', '4', '5']
const statusLabels = ['全部状态', '待支付', '待使用', '使用中', '已完成', '已取消', '已超时']

export default {
  data() {
    return {
      loading: false,
      orders: [],
      total: 0,
      statusLabels,
      statusIndex: 0,
      filters: {
        orderNo: '',
        status: '',
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  computed: {
    totalPages() {
      const pages = Math.ceil(this.total / this.filters.pageSize)
      return pages > 0 ? pages : 1
    }
  },
  onShow() {
    if (!this.ensureAdmin()) return
    this.fetchOrders()
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
    onStatusChange(event) {
      this.statusIndex = Number(event.detail.value || 0)
      this.filters.status = statusValues[this.statusIndex]
      this.filters.pageNum = 1
      this.fetchOrders()
    },
    handleSearch() {
      this.filters.pageNum = 1
      this.fetchOrders()
    },
    async fetchOrders() {
      this.loading = true
      try {
        const res = await getOrderList(this.filters)
        const page = res.data || {}
        this.orders = page.records || []
        this.total = Number(page.total || 0)
      } catch (error) {
        uni.showToast({ title: error?.message || '获取订单失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async viewDetail(orderId) {
      try {
        const res = await getOrderDetail(orderId)
        const d = res.data || {}
        const text = [
          `订单号: ${d.orderNo || '-'}`,
          `状态: ${this.statusText(d.status)}`,
          `用户ID: ${d.userId || '-'}`,
          `车位ID: ${d.spaceId || '-'}`,
          `金额: ¥${this.money(d.totalAmount)}`,
          `开始: ${this.formatTime(d.startTime)}`,
          `结束: ${this.formatTime(d.endTime)}`
        ].join('\n')
        uni.showModal({ title: '订单详情', content: text, showCancel: false })
      } catch (error) {
        uni.showToast({ title: error?.message || '获取详情失败', icon: 'none' })
      }
    },
    prevPage() {
      if (this.filters.pageNum <= 1) return
      this.filters.pageNum -= 1
      this.fetchOrders()
    },
    nextPage() {
      if (this.filters.pageNum >= this.totalPages) return
      this.filters.pageNum += 1
      this.fetchOrders()
    },
    money(value) {
      const num = Number(value || 0)
      return Number.isFinite(num) ? num.toFixed(2) : '0.00'
    },
    formatTime(value) {
      if (!value) return '-'
      return String(value).replace('T', ' ').slice(0, 19)
    },
    statusText(status) {
      const map = {
        0: '待支付',
        1: '待使用',
        2: '使用中',
        3: '已完成',
        4: '已取消',
        5: '已超时'
      }
      return map[Number(status)] || '未知'
    },
    statusClass(status) {
      const key = Number(status)
      if (key === 3) return 'ok'
      if (key === 4 || key === 5) return 'bad'
      if (key === 2 || key === 1) return 'warn'
      return ''
    }
  }
}
</script>

<style scoped>
.order-page {
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

.filters {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.filter-input {
  flex: 1;
  height: 64rpx;
  border-radius: 10rpx;
  background: #f5f7fb;
  padding: 0 16rpx;
  font-size: 24rpx;
}

.filter-picker {
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 10rpx;
  background: #f5f7fb;
  padding: 0 16rpx;
  font-size: 24rpx;
  color: #344054;
}

.search-btn {
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 10rpx;
  background: #1f6fff;
  color: #fff;
  font-size: 24rpx;
  padding: 0 20rpx;
  border: none;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.summary-text {
  font-size: 24rpx;
  color: #667085;
}

.order-item {
  border: 1rpx solid #edf0f5;
  border-radius: 10rpx;
  padding: 16rpx;
  margin-bottom: 12rpx;
}

.order-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-no {
  font-size: 26rpx;
  font-weight: 600;
  color: #1f2937;
}

.status-tag {
  font-size: 22rpx;
  color: #64748b;
  background: #f1f5f9;
  border-radius: 20rpx;
  padding: 4rpx 12rpx;
}

.status-tag.ok {
  color: #0f9d58;
  background: #e8f8ef;
}

.status-tag.warn {
  color: #b7791f;
  background: #fff8e6;
}

.status-tag.bad {
  color: #dc2626;
  background: #ffebee;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  margin-top: 12rpx;
  color: #475467;
  font-size: 22rpx;
}

.actions {
  margin-top: 12rpx;
}

.tiny-btn {
  height: 52rpx;
  line-height: 52rpx;
  border-radius: 26rpx;
  background: #eef5ff;
  color: #1f6fff;
  font-size: 22rpx;
  border: none;
  padding: 0 18rpx;
}

.pager {
  margin-top: 10rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16rpx;
}

.pager-text {
  font-size: 24rpx;
  color: #475467;
}
</style>
