<template>
  <view class="verify-page">
    <view class="card">
      <text class="card-title">订单核销</text>
      <view class="form-item">
        <text class="label">订单号</text>
        <input class="input" v-model="orderNo" placeholder="请输入订单号" />
      </view>
      <view class="form-item">
        <text class="label">核销码</text>
        <input class="input" v-model="verifyCode" placeholder="请输入核销码" />
      </view>
      <view class="form-actions">
        <button class="btn-secondary" @click="queryOrder" :loading="loading">查询订单</button>
        <button class="btn-primary" @click="verifyOrder" :loading="verifying">核销订单</button>
      </view>
    </view>

    <view class="card" v-if="order">
      <text class="card-title">订单信息</text>
      <view class="info-row">
        <text class="info-label">订单号</text>
        <text class="info-value">{{ order.orderNo }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">状态</text>
        <text class="info-value" :class="statusClass(order.status)">{{ statusText(order.status) }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">车位地址</text>
        <text class="info-value">{{ order.spaceAddress || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">预约时间</text>
        <text class="info-value">{{ formatDateTime(order.startTime) }} - {{ formatDateTime(order.endTime) }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">实付金额</text>
        <text class="info-value">¥{{ order.actualAmount || order.amount }}</text>
      </view>
      <view class="form-actions">
        <button class="btn-secondary" @click="completeOrder" v-if="order.status === 2">完成订单</button>
      </view>
    </view>
  </view>
</template>

<script>
import { getReservationByOrderNo, getReservationDetail, verifyReservation, completeReservation } from '../../api/reservation.js'

export default {
  data() {
    return {
      orderId: null,
      orderNo: '',
      verifyCode: '',
      order: null,
      loading: false,
      verifying: false
    }
  },
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id
      this.loadById(options.id)
    }
    if (options.orderNo) {
      this.orderNo = options.orderNo
    }
  },
  methods: {
    async loadById(id) {
      this.loading = true
      try {
        const res = await getReservationDetail(id)
        if (res.code === 200) {
          this.order = res.data
          this.orderNo = res.data.orderNo || this.orderNo
        }
      } catch (e) {
        uni.showToast({ title: '获取订单失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async queryOrder() {
      if (!this.orderNo) {
        uni.showToast({ title: '请输入订单号', icon: 'none' })
        return
      }
      this.loading = true
      try {
        const res = await getReservationByOrderNo(this.orderNo)
        if (res.code === 200) {
          this.order = res.data
          this.orderId = res.data.id
        } else {
          uni.showToast({ title: res.message || '订单不存在', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '查询失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async verifyOrder() {
      if (!this.orderId) {
        await this.queryOrder()
        if (!this.orderId) return
      }
      if (!this.verifyCode) {
        uni.showToast({ title: '请输入核销码', icon: 'none' })
        return
      }
      this.verifying = true
      try {
        const res = await verifyReservation(this.orderId, this.verifyCode)
        if (res.code === 200) {
          uni.showToast({ title: '核销成功', icon: 'success' })
          await this.loadById(this.orderId)
        } else {
          uni.showToast({ title: res.message || '核销失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '核销失败', icon: 'none' })
      } finally {
        this.verifying = false
      }
    },
    completeOrder() {
      if (!this.orderId) return
      uni.showModal({
        title: '完成订单',
        content: '确认完成该订单？',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await completeReservation(this.orderId)
            if (result.code === 200) {
              uni.showToast({ title: '订单已完成', icon: 'success' })
              await this.loadById(this.orderId)
            } else {
              uni.showToast({ title: result.message || '操作失败', icon: 'none' })
            }
          } catch (e) {
            uni.showToast({ title: '操作失败', icon: 'none' })
          }
        }
      })
    },
    statusText(status) {
      const map = { 0: '待支付', 1: '待使用', 2: '使用中', 3: '已完成', 4: '已取消', 5: '已超时' }
      return map[status] || '未知'
    },
    statusClass(status) {
      const map = { 0: 'pending', 1: 'pending', 2: 'using', 3: 'completed', 4: 'cancelled', 5: 'cancelled' }
      return map[status] || ''
    },
    formatDateTime(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      return `${month}-${day} ${hour}:${minute}`
    }
  }
}
</script>

<style scoped>
.verify-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.form-item {
  margin-bottom: 20rpx;
}

.label {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: block;
}

.input {
  width: 100%;
  height: 76rpx;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  background: #fafafa;
}

.form-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 10rpx;
}

.btn-primary,
.btn-secondary {
  flex: 1;
  height: 72rpx;
  line-height: 72rpx;
  border-radius: 36rpx;
  font-size: 26rpx;
  border: none;
}

.btn-primary {
  background: #007bff;
  color: #fff;
}

.btn-secondary {
  background: #f5f5f5;
  color: #666;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
}

.info-label {
  font-size: 26rpx;
  color: #999;
}

.info-value {
  font-size: 26rpx;
  color: #333;
}

.info-value.pending { color: #ff9800; }
.info-value.using { color: #2196f3; }
.info-value.completed { color: #4caf50; }
.info-value.cancelled { color: #f44336; }
</style>
