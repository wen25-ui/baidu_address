<template>
  <view class="create-page">
    <!-- 车位信息卡片 -->
    <view class="space-card">
      <text class="card-title">预约车位</text>
      <view class="space-info">
        <text class="space-price">¥{{ price }}/小时</text>
      </view>
    </view>

    <!-- 时间选择 -->
    <view class="time-card">
      <text class="card-title">选择时间</text>
      
      <view class="time-picker">
        <view class="picker-item" @click="showStartPicker = true">
          <text class="picker-label">开始时间</text>
          <view class="picker-value">
            <text>{{ formatDateTime(startTime) }}</text>
            <text class="iconfont icon-arrow-right"></text>
          </view>
        </view>
        
        <view class="picker-item" @click="showEndPicker = true">
          <text class="picker-label">结束时间</text>
          <view class="picker-value">
            <text>{{ formatDateTime(endTime) }}</text>
            <text class="iconfont icon-arrow-right"></text>
          </view>
        </view>
      </view>
      
      <view class="duration-info" v-if="duration > 0">
        <text>预约时长：{{ duration }}小时</text>
      </view>
    </view>

    <!-- 费用明细 -->
    <view class="fee-card">
      <text class="card-title">费用明细</text>
      <view class="fee-row">
        <text class="fee-label">停车费用</text>
        <text class="fee-value">¥{{ parkingFee }}</text>
      </view>
      <view class="fee-row total">
        <text class="fee-label">合计</text>
        <text class="fee-value total-value">¥{{ totalFee }}</text>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-bar">
      <view class="total-info">
        <text class="total-label">合计：</text>
        <text class="total-amount">¥{{ totalFee }}</text>
      </view>
      <button class="submit-btn" @click="submitOrder" :loading="submitting">
        提交订单
      </button>
    </view>

    <!-- 日期时间选择器 -->
    <uni-datetime-picker 
      type="datetime" 
      v-model="startTime"
      :start="minStartTime"
      @change="onStartTimeChange"
      v-if="showStartPicker"
    />
    <uni-datetime-picker 
      type="datetime" 
      v-model="endTime"
      :start="minEndTime"
      @change="onEndTimeChange"
      v-if="showEndPicker"
    />
  </view>
</template>

<script>
import { createReservation, checkAvailable } from '../../api/reservation.js'
import { getWallet } from '../../api/wallet.js'

export default {
  data() {
    return {
      spaceId: null,
      price: 0,
      startTime: '',
      endTime: '',
      showStartPicker: false,
      showEndPicker: false,
      submitting: false
    }
  },
  computed: {
    minStartTime() {
      return new Date().toISOString().slice(0, 16)
    },
    minEndTime() {
      if (!this.startTime) return this.minStartTime
      const start = new Date(this.startTime)
      start.setHours(start.getHours() + 1)
      return start.toISOString().slice(0, 16)
    },
    duration() {
      if (!this.startTime || !this.endTime) return 0
      const start = new Date(this.startTime)
      const end = new Date(this.endTime)
      const hours = (end - start) / (1000 * 60 * 60)
      return Math.max(0, Math.round(hours * 10) / 10)
    },
    parkingFee() {
      return (this.duration * this.price).toFixed(2)
    },
    totalFee() {
      return this.parkingFee
    }
  },
  onLoad(options) {
    this.spaceId = options.spaceId
    this.price = parseFloat(options.price) || 0
    this.initTime()
  },
  methods: {
    initTime() {
      const now = new Date()
      now.setMinutes(Math.ceil(now.getMinutes() / 30) * 30, 0, 0)
      this.startTime = this.formatDateForPicker(now)
      
      const end = new Date(now)
      end.setHours(end.getHours() + 2)
      this.endTime = this.formatDateForPicker(end)
    },
    formatDateForPicker(date) {
      const pad = n => n.toString().padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
    },
    formatDateTime(dateStr) {
      if (!dateStr) return '请选择'
      const date = new Date(dateStr)
      const month = date.getMonth() + 1
      const day = date.getDate()
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      return `${month}月${day}日 ${hour}:${minute}`
    },
    onStartTimeChange(e) {
      this.startTime = e
      this.showStartPicker = false
      // 如果结束时间早于开始时间，自动调整
      if (this.endTime && new Date(this.endTime) <= new Date(this.startTime)) {
        const end = new Date(this.startTime)
        end.setHours(end.getHours() + 2)
        this.endTime = this.formatDateForPicker(end)
      }
    },
    onEndTimeChange(e) {
      this.endTime = e
      this.showEndPicker = false
    },
    async submitOrder() {
      if (!this.startTime || !this.endTime) {
        return uni.showToast({ title: '请选择预约时间', icon: 'none' })
      }
      
      if (this.duration <= 0) {
        return uni.showToast({ title: '结束时间必须晚于开始时间', icon: 'none' })
      }

      // 检查余额
      try {
        const walletRes = await getWallet()
        if (walletRes.code === 200) {
          const balance = parseFloat(walletRes.data.balance) || 0
          if (balance < parseFloat(this.totalFee)) {
            uni.showModal({
              title: '余额不足',
              content: `当前余额¥${balance.toFixed(2)}，需要¥${this.totalFee}，是否去充值？`,
              success: (res) => {
                if (res.confirm) {
                  uni.navigateTo({ url: '/pages/wallet/recharge' })
                }
              }
            })
            return
          }
        }
      } catch (e) {
        console.error('检查余额失败:', e)
      }

      // 检查时段可用性
      try {
        const checkRes = await checkAvailable(
          this.spaceId,
          this.startTime.replace(' ', 'T') + ':00',
          this.endTime.replace(' ', 'T') + ':00'
        )
        if (checkRes.code === 200 && !checkRes.data) {
          return uni.showToast({ title: '该时段已被预约', icon: 'none' })
        }
      } catch (e) {
        console.error('检查可用性失败:', e)
      }

      // 提交订单
      this.submitting = true
      try {
        const res = await createReservation({
          spaceId: parseInt(this.spaceId),
          startTime: this.startTime.replace(' ', 'T') + ':00',
          endTime: this.endTime.replace(' ', 'T') + ':00'
        })
        
        if (res.code === 200) {
          uni.showToast({ title: '下单成功', icon: 'success' })
          setTimeout(() => {
            uni.redirectTo({ 
              url: `/pages/reservation/detail?id=${res.data.id}&needPay=1` 
            })
          }, 1500)
        } else {
          uni.showToast({ title: res.message || '下单失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '下单失败，请重试', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.create-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 160rpx;
}

.space-card, .time-card, .fee-card {
  background: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.space-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.space-price {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.time-picker {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.picker-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
}

.picker-label {
  font-size: 28rpx;
  color: #666;
}

.picker-value {
  display: flex;
  align-items: center;
  font-size: 28rpx;
  color: #333;
}

.picker-value .iconfont {
  margin-left: 8rpx;
  color: #999;
}

.duration-info {
  margin-top: 20rpx;
  padding: 16rpx;
  background: #e3f2fd;
  border-radius: 8rpx;
  text-align: center;
  font-size: 26rpx;
  color: #1976d2;
}

.fee-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.fee-row:last-child {
  border-bottom: none;
}

.fee-label {
  font-size: 28rpx;
  color: #666;
}

.fee-value {
  font-size: 28rpx;
  color: #333;
}

.fee-row.total {
  margin-top: 10rpx;
  padding-top: 20rpx;
}

.total-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.08);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.total-info {
  display: flex;
  align-items: baseline;
}

.total-label {
  font-size: 28rpx;
  color: #666;
}

.total-amount {
  font-size: 40rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.submit-btn {
  width: 280rpx;
  height: 88rpx;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 500;
  border-radius: 44rpx;
  border: none;
}
</style>
