<template>
  <view class="detail-page">
    <view class="loading" v-if="loading">
      <text>加载中...</text>
    </view>
    
    <template v-else>
      <!-- 订单状态 -->
      <view class="status-card" :class="statusClass">
        <text class="status-text">{{ statusText }}</text>
        <text class="status-desc">{{ statusDesc }}</text>
      </view>

      <!-- 车位信息 -->
      <view class="section-card">
        <text class="section-title">车位信息</text>
        <view class="space-info">
          <view class="info-row">
            <text class="info-label">车位地址</text>
            <text class="info-value">{{ order.spaceAddress || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">车位编号</text>
            <text class="info-value">{{ order.spaceNo || '-' }}</text>
          </view>
        </view>
      </view>

      <!-- 预约时间 -->
      <view class="section-card">
        <text class="section-title">预约时间</text>
        <view class="time-info">
          <view class="time-item">
            <text class="time-label">开始时间</text>
            <text class="time-value">{{ formatDateTime(order.startTime) }}</text>
          </view>
          <view class="time-divider">
            <view class="divider-line"></view>
            <text class="divider-text">{{ duration }}小时</text>
            <view class="divider-line"></view>
          </view>
          <view class="time-item">
            <text class="time-label">结束时间</text>
            <text class="time-value">{{ formatDateTime(order.endTime) }}</text>
          </view>
        </view>
      </view>

      <!-- 费用明细 -->
      <view class="section-card">
        <text class="section-title">费用明细</text>
        <view class="fee-info">
          <view class="fee-row">
            <text class="fee-label">停车费用</text>
            <text class="fee-value">¥{{ order.amount || '0.00' }}</text>
          </view>
          <view class="fee-row total">
            <text class="fee-label">实付金额</text>
            <text class="fee-value highlight">¥{{ order.actualAmount || order.amount || '0.00' }}</text>
          </view>
        </view>
      </view>

      <!-- 订单信息 -->
      <view class="section-card">
        <text class="section-title">订单信息</text>
        <view class="order-info">
          <view class="info-row">
            <text class="info-label">订单编号</text>
            <text class="info-value copy" @click="copyOrderNo">{{ order.orderNo }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">下单时间</text>
            <text class="info-value">{{ formatDateTime(order.createTime) }}</text>
          </view>
          <view class="info-row" v-if="order.payTime">
            <text class="info-label">支付时间</text>
            <text class="info-value">{{ formatDateTime(order.payTime) }}</text>
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-bar" v-if="showActions">
        <!-- 待支付 -->
        <template v-if="order.status === 0">
          <button class="action-btn cancel" @click="cancelOrder">取消订单</button>
          <button class="action-btn pay" @click="payOrder">立即支付</button>
        </template>
        
        <!-- 已支付/使用中 -->
        <template v-if="order.status === 1 || order.status === 2">
          <button class="action-btn qrcode" @click="showQrcode">出示二维码</button>
          <button class="action-btn cancel" @click="cancelOrder" v-if="order.status === 1">取消预约</button>
        </template>

        <!-- 已完成 -->
        <template v-if="order.status === 3">
          <button class="action-btn again" @click="orderAgain">再次预约</button>
        </template>
      </view>
    </template>

    <!-- 二维码弹窗 -->
    <uni-popup ref="qrcodePopup" type="center">
      <view class="qrcode-modal">
        <text class="qrcode-title">入场二维码</text>
        <canvas canvas-id="qrcodeCanvas" class="qrcode-canvas"></canvas>
        <text class="qrcode-tip">请向车位主出示此二维码</text>
        <button class="qrcode-close" @click="closeQrcode">关闭</button>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { getReservationDetail, payReservation, cancelReservation } from '../../api/reservation.js'

export default {
  data() {
    return {
      orderId: null,
      order: {},
      loading: true,
      needPay: false
    }
  },
  computed: {
    statusClass() {
      const statusMap = {
        0: 'pending',
        1: 'paid',
        2: 'using',
        3: 'completed',
        4: 'cancelled'
      }
      return statusMap[this.order.status] || ''
    },
    statusText() {
      const textMap = {
        0: '待支付',
        1: '待使用',
        2: '使用中',
        3: '已完成',
        4: '已取消'
      }
      return textMap[this.order.status] || '未知'
    },
    statusDesc() {
      const descMap = {
        0: '请在15分钟内完成支付',
        1: '请按时到达车位',
        2: '正在使用车位中',
        3: '感谢使用，欢迎再次预约',
        4: '订单已取消'
      }
      return descMap[this.order.status] || ''
    },
    duration() {
      if (!this.order.startTime || !this.order.endTime) return 0
      const start = new Date(this.order.startTime)
      const end = new Date(this.order.endTime)
      const hours = (end - start) / (1000 * 60 * 60)
      return Math.round(hours * 10) / 10
    },
    showActions() {
      return [0, 1, 2, 3].includes(this.order.status)
    }
  },
  onLoad(options) {
    this.orderId = options.id
    this.needPay = options.needPay === '1'
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      this.loading = true
      try {
        const res = await getReservationDetail(this.orderId)
        if (res.code === 200) {
          this.order = res.data
          
          // 自动弹出支付
          if (this.needPay && this.order.status === 0) {
            setTimeout(() => this.payOrder(), 500)
          }
        } else {
          uni.showToast({ title: res.message || '获取订单详情失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '获取订单详情失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    formatDateTime(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    },
    copyOrderNo() {
      uni.setClipboardData({
        data: this.order.orderNo,
        success: () => {
          uni.showToast({ title: '已复制', icon: 'success' })
        }
      })
    },
    async payOrder() {
      uni.showLoading({ title: '支付中...' })
      try {
        const res = await payReservation(this.orderId)
        if (res.code === 200) {
          uni.showToast({ title: '支付成功', icon: 'success' })
          this.loadDetail()
        } else {
          uni.showToast({ title: res.message || '支付失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '支付失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    async cancelOrder() {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消此订单吗？',
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({ title: '取消中...' })
            try {
              const res = await cancelReservation(this.orderId)
              if (res.code === 200) {
                uni.showToast({ title: '取消成功', icon: 'success' })
                this.loadDetail()
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
    showQrcode() {
      this.$refs.qrcodePopup.open()
      this.$nextTick(() => {
        this.generateQrcode()
      })
    },
    closeQrcode() {
      this.$refs.qrcodePopup.close()
    },
    generateQrcode() {
      // 简单的二维码展示（实际项目应使用二维码库）
      const ctx = uni.createCanvasContext('qrcodeCanvas', this)
      ctx.fillStyle = '#fff'
      ctx.fillRect(0, 0, 200, 200)
      ctx.fillStyle = '#333'
      ctx.setFontSize(14)
      ctx.setTextAlign('center')
      ctx.fillText('订单号:', 100, 90)
      ctx.fillText(this.order.orderNo || '', 100, 110)
      ctx.draw()
    },
    orderAgain() {
      uni.navigateTo({
        url: `/pages/parking/detail?id=${this.order.spaceId}`
      })
    }
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 160rpx;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400rpx;
  color: #999;
}

.status-card {
  padding: 60rpx 30rpx;
  text-align: center;
  color: #fff;
}

.status-card.pending { background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%); }
.status-card.paid { background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%); }
.status-card.using { background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%); }
.status-card.completed { background: linear-gradient(135deg, #9e9e9e 0%, #757575 100%); }
.status-card.cancelled { background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%); }

.status-text {
  font-size: 40rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 16rpx;
}

.status-desc {
  font-size: 26rpx;
  opacity: 0.9;
}

.section-card {
  background: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 24rpx;
  display: block;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
}

.info-label {
  font-size: 28rpx;
  color: #999;
}

.info-value {
  font-size: 28rpx;
  color: #333;
}

.info-value.copy {
  color: #007bff;
}

.time-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.time-item {
  text-align: center;
}

.time-label {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-bottom: 8rpx;
}

.time-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.time-divider {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  margin: 0 20rpx;
}

.divider-line {
  width: 60rpx;
  height: 2rpx;
  background: #e0e0e0;
}

.divider-text {
  font-size: 22rpx;
  color: #007bff;
  margin: 8rpx 0;
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

.fee-value.highlight {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx 30rpx;
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.08);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.action-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
  font-weight: 500;
  border: none;
}

.action-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.action-btn.pay {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: #fff;
}

.action-btn.qrcode {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: #fff;
}

.action-btn.again {
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  color: #fff;
}

.qrcode-modal {
  width: 500rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  text-align: center;
}

.qrcode-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 30rpx;
}

.qrcode-canvas {
  width: 200px;
  height: 200px;
  margin: 0 auto;
}

.qrcode-tip {
  font-size: 26rpx;
  color: #999;
  display: block;
  margin-top: 20rpx;
}

.qrcode-close {
  margin-top: 30rpx;
  background: #f5f5f5;
  color: #666;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  border: none;
}
</style>
