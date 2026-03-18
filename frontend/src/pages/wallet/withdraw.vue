<template>
  <view class="withdraw-page">
    <!-- 可提现余额 -->
    <view class="balance-card">
      <text class="balance-label">可提现余额</text>
      <text class="balance-value">¥{{ balance }}</text>
    </view>

    <!-- 提现金额 -->
    <view class="section">
      <text class="section-title">提现金额</text>
      <view class="amount-input-wrap">
        <text class="currency">¥</text>
        <input 
          type="digit" 
          class="amount-input"
          placeholder="请输入提现金额"
          v-model="amount"
        />
        <text class="withdraw-all" @click="withdrawAll">全部提现</text>
      </view>
      <view class="amount-tips">
        <text>最低提现金额：¥10.00</text>
        <text>手续费：{{ feeRate }}%</text>
      </view>
    </view>

    <!-- 提现到账 -->
    <view class="section">
      <text class="section-title">提现到</text>
      <view class="withdraw-target">
        <image src="/static/images/wechat-pay.png" class="target-icon" mode="aspectFit"></image>
        <text class="target-name">微信零钱</text>
        <text class="target-desc">实时到账</text>
      </view>
    </view>

    <!-- 费用明细 -->
    <view class="section" v-if="amount > 0">
      <text class="section-title">费用明细</text>
      <view class="fee-info">
        <view class="fee-row">
          <text class="fee-label">提现金额</text>
          <text class="fee-value">¥{{ amount }}</text>
        </view>
        <view class="fee-row">
          <text class="fee-label">手续费（{{ feeRate }}%）</text>
          <text class="fee-value">-¥{{ fee }}</text>
        </view>
        <view class="fee-row total">
          <text class="fee-label">实际到账</text>
          <text class="fee-value highlight">¥{{ actualAmount }}</text>
        </view>
      </view>
    </view>

    <!-- 提现按钮 -->
    <view class="submit-bar">
      <button class="submit-btn" @click="doWithdraw" :loading="loading" :disabled="!canWithdraw">
        确认提现
      </button>
    </view>

    <!-- 提现说明 -->
    <view class="tips-section">
      <text class="tips-title">提现说明</text>
      <view class="tips-list">
        <text class="tip-item">1. 提现金额最低10元，最高单日5000元</text>
        <text class="tip-item">2. 提现将收取{{ feeRate }}%手续费</text>
        <text class="tip-item">3. 提现申请后预计1-3个工作日到账</text>
        <text class="tip-item">4. 如有疑问请联系客服</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getWallet, withdraw } from '../../api/wallet.js'

export default {
  data() {
    return {
      balance: '0.00',
      balanceNum: 0,
      amount: '',
      feeRate: 0.5,
      loading: false
    }
  },
  computed: {
    fee() {
      const amt = parseFloat(this.amount) || 0
      return (amt * this.feeRate / 100).toFixed(2)
    },
    actualAmount() {
      const amt = parseFloat(this.amount) || 0
      const fee = amt * this.feeRate / 100
      return (amt - fee).toFixed(2)
    },
    canWithdraw() {
      const amt = parseFloat(this.amount) || 0
      return amt >= 10 && amt <= this.balanceNum
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
          this.balanceNum = res.data.balance || 0
          this.balance = this.balanceNum.toFixed(2)
        }
      } catch (e) {
        console.error('加载余额失败:', e)
      }
    },
    withdrawAll() {
      this.amount = this.balance
    },
    async doWithdraw() {
      const amt = parseFloat(this.amount) || 0
      
      if (amt < 10) {
        return uni.showToast({ title: '最低提现10元', icon: 'none' })
      }
      
      if (amt > this.balanceNum) {
        return uni.showToast({ title: '余额不足', icon: 'none' })
      }

      uni.showModal({
        title: '确认提现',
        content: `提现金额：¥${amt}\n手续费：¥${this.fee}\n实际到账：¥${this.actualAmount}`,
        success: async (res) => {
          if (res.confirm) {
            this.loading = true
            try {
              const res = await withdraw({ amount: amt })
              
              if (res.code === 200) {
                uni.showToast({ title: '提现申请已提交', icon: 'success' })
                setTimeout(() => {
                  uni.navigateBack()
                }, 1500)
              } else {
                uni.showToast({ title: res.message || '提现失败', icon: 'none' })
              }
            } catch (e) {
              uni.showToast({ title: '提现失败', icon: 'none' })
            } finally {
              this.loading = false
            }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.withdraw-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 160rpx;
}

.balance-card {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
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

.amount-input-wrap {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  height: 100rpx;
}

.currency {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
  margin-right: 12rpx;
}

.amount-input {
  flex: 1;
  font-size: 40rpx;
  font-weight: bold;
}

.withdraw-all {
  font-size: 28rpx;
  color: #007bff;
}

.amount-tips {
  display: flex;
  justify-content: space-between;
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #999;
}

.withdraw-target {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
}

.target-icon {
  width: 60rpx;
  height: 60rpx;
}

.target-name {
  flex: 1;
  margin-left: 20rpx;
  font-size: 28rpx;
  color: #333;
}

.target-desc {
  font-size: 24rpx;
  color: #4caf50;
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

.fee-value.highlight {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 30rpx;
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.08);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 500;
  border-radius: 44rpx;
  border: none;
}

.submit-btn[disabled] {
  background: #ccc;
}

.tips-section {
  margin: 20rpx;
  padding: 30rpx;
}

.tips-title {
  font-size: 26rpx;
  color: #999;
  display: block;
  margin-bottom: 16rpx;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.tip-item {
  font-size: 24rpx;
  color: #999;
}
</style>
