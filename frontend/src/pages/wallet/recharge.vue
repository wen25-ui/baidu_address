<template>
  <view class="recharge-page">
    <!-- 余额显示 -->
    <view class="balance-card">
      <text class="balance-label">当前余额</text>
      <text class="balance-value">¥{{ balance }}</text>
    </view>

    <!-- 充值金额选择 -->
    <view class="section">
      <text class="section-title">选择充值金额</text>
      <view class="amount-grid">
        <view 
          class="amount-item" 
          v-for="item in amountOptions" 
          :key="item"
          :class="{ active: selectedAmount === item }"
          @click="selectAmount(item)"
        >
          <text class="amount-value">¥{{ item }}</text>
        </view>
      </view>
      
      <view class="custom-amount">
        <text class="custom-label">自定义金额</text>
        <view class="custom-input-wrap">
          <text class="currency">¥</text>
          <input 
            type="digit" 
            class="custom-input"
            placeholder="请输入金额"
            v-model="customAmount"
            @input="onCustomInput"
          />
        </view>
      </view>
    </view>

    <!-- 支付方式 -->
    <view class="section">
      <text class="section-title">支付方式</text>
      <view class="pay-methods">
        <view 
          class="pay-item" 
          :class="{ active: payMethod === 'wechat' }"
          @click="payMethod = 'wechat'"
        >
          <image src="/static/images/wechat-pay.png" class="pay-icon" mode="aspectFit"></image>
          <text class="pay-name">微信支付</text>
          <view class="pay-check" v-if="payMethod === 'wechat'"></view>
        </view>
        <view 
          class="pay-item" 
          :class="{ active: payMethod === 'wallet' }"
          @click="payMethod = 'wallet'"
        >
          <view class="pay-icon wallet-icon">
            <text class="iconfont icon-wallet"></text>
          </view>
          <text class="pay-name">模拟支付（测试）</text>
          <view class="pay-check" v-if="payMethod === 'wallet'"></view>
        </view>
      </view>
    </view>

    <!-- 充值按钮 -->
    <view class="submit-bar">
      <view class="total-info">
        <text class="total-label">充值金额：</text>
        <text class="total-value">¥{{ finalAmount }}</text>
      </view>
      <button class="submit-btn" @click="doRecharge" :loading="loading">
        立即充值
      </button>
    </view>
  </view>
</template>

<script>
import { getWallet, recharge } from '../../api/wallet.js'

export default {
  data() {
    return {
      balance: '0.00',
      amountOptions: [10, 20, 50, 100, 200, 500],
      selectedAmount: null,
      customAmount: '',
      payMethod: 'wallet',
      loading: false
    }
  },
  computed: {
    finalAmount() {
      if (this.customAmount) {
        return parseFloat(this.customAmount) || 0
      }
      return this.selectedAmount || 0
    }
  },
  onLoad() {
    this.loadBalance()
  },
  methods: {
    async loadBalance() {
      try {
        const res = await getWallet()
        if (res.code === 200) {
          this.balance = (res.data.balance || 0).toFixed(2)
        }
      } catch (e) {
        console.error('加载余额失败:', e)
      }
    },
    selectAmount(amount) {
      this.selectedAmount = amount
      this.customAmount = ''
    },
    onCustomInput() {
      this.selectedAmount = null
    },
    async doRecharge() {
      if (this.finalAmount <= 0) {
        return uni.showToast({ title: '请选择充值金额', icon: 'none' })
      }
      
      if (this.finalAmount < 1) {
        return uni.showToast({ title: '最低充值1元', icon: 'none' })
      }

      this.loading = true
      try {
        const res = await recharge({ amount: this.finalAmount })
        
        if (res.code === 200) {
          uni.showToast({ title: '充值成功', icon: 'success' })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } else {
          uni.showToast({ title: res.message || '充值失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '充值失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.recharge-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 160rpx;
}

.balance-card {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  padding: 40rpx;
  color: #fff;
}

.balance-label {
  font-size: 26rpx;
  opacity: 0.9;
  display: block;
}

.balance-value {
  font-size: 56rpx;
  font-weight: bold;
  margin-top: 12rpx;
  display: block;
}

.section {
  background: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 24rpx;
}

.amount-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
}

.amount-item {
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 12rpx;
  border: 2rpx solid transparent;
}

.amount-item.active {
  border-color: #007bff;
  background: #e3f2fd;
}

.amount-item .amount-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.amount-item.active .amount-value {
  color: #007bff;
}

.custom-amount {
  margin-top: 30rpx;
}

.custom-label {
  font-size: 28rpx;
  color: #666;
  display: block;
  margin-bottom: 16rpx;
}

.custom-input-wrap {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  height: 88rpx;
}

.currency {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-right: 12rpx;
}

.custom-input {
  flex: 1;
  font-size: 32rpx;
}

.pay-methods {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.pay-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  border: 2rpx solid transparent;
}

.pay-item.active {
  border-color: #007bff;
  background: #e3f2fd;
}

.pay-icon {
  width: 60rpx;
  height: 60rpx;
}

.wallet-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #007bff;
  border-radius: 12rpx;
  color: #fff;
  font-size: 32rpx;
}

.pay-name {
  flex: 1;
  margin-left: 20rpx;
  font-size: 28rpx;
  color: #333;
}

.pay-check {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: #007bff;
  position: relative;
}

.pay-check::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -60%) rotate(45deg);
  width: 10rpx;
  height: 18rpx;
  border-right: 3rpx solid #fff;
  border-bottom: 3rpx solid #fff;
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

.total-value {
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
