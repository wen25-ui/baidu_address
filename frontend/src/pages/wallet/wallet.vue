<template>
  <view class="wallet-page">
    <!-- 钱包卡片 -->
    <view class="wallet-card">
      <view class="card-bg"></view>
      <view class="card-content">
        <text class="balance-label">账户余额（元）</text>
        <view class="balance-row">
          <text class="balance-value">{{ balance }}</text>
        </view>
        <view class="action-btns">
          <button class="wallet-btn recharge" @click="goRecharge">充值</button>
          <button class="wallet-btn withdraw" @click="goWithdraw">提现</button>
        </view>
      </view>
    </view>

    <!-- 收益概览 -->
    <view class="income-card">
      <view class="income-item">
        <text class="income-value">{{ todayIncome }}</text>
        <text class="income-label">今日收益</text>
      </view>
      <view class="divider"></view>
      <view class="income-item">
        <text class="income-value">{{ monthIncome }}</text>
        <text class="income-label">本月收益</text>
      </view>
      <view class="divider"></view>
      <view class="income-item">
        <text class="income-value">{{ totalIncome }}</text>
        <text class="income-label">累计收益</text>
      </view>
    </view>

    <!-- 交易记录 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">交易记录</text>
        <text class="section-more" @click="goTransactions">更多</text>
      </view>
      
      <view class="transaction-list">
        <view v-if="transactions.length === 0" class="empty-state">
          <text>暂无交易记录</text>
        </view>
        
        <view class="transaction-item" v-for="item in transactions" :key="item.id">
          <view class="trans-icon" :class="getTypeClass(item.type)">
            <text class="iconfont" :class="getTypeIcon(item.type)"></text>
          </view>
          <view class="trans-info">
            <text class="trans-title">{{ getTypeText(item.type) }}</text>
            <text class="trans-time">{{ formatTime(item.createTime) }}</text>
          </view>
          <text class="trans-amount" :class="{ income: item.amount > 0 }">
            {{ item.amount > 0 ? '+' : '' }}{{ item.amount }}
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getWallet, getTransactions } from '../../api/wallet.js'

export default {
  data() {
    return {
      balance: '0.00',
      todayIncome: '0.00',
      monthIncome: '0.00',
      totalIncome: '0.00',
      transactions: []
    }
  },
  onShow() {
    this.loadWallet()
    this.loadTransactions()
  },
  methods: {
    async loadWallet() {
      try {
        const res = await getWallet()
        if (res.code === 200) {
          const data = res.data || {}
          this.balance = (data.balance || 0).toFixed(2)
          this.todayIncome = (data.todayIncome || 0).toFixed(2)
          this.monthIncome = (data.monthIncome || 0).toFixed(2)
          this.totalIncome = (data.totalIncome || 0).toFixed(2)
        }
      } catch (e) {
        console.error('加载钱包失败:', e)
      }
    },
    async loadTransactions() {
      try {
        const res = await getTransactions({ page: 1, pageSize: 5 })
        if (res.code === 200) {
          this.transactions = res.data.records || res.data || []
        }
      } catch (e) {
        console.error('加载交易记录失败:', e)
      }
    },
    getTypeClass(type) {
      const classMap = {
        1: 'recharge',
        2: 'withdraw',
        3: 'income',
        4: 'expense'
      }
      return classMap[type] || ''
    },
    getTypeIcon(type) {
      const iconMap = {
        1: 'icon-recharge',
        2: 'icon-withdraw',
        3: 'icon-income',
        4: 'icon-expense'
      }
      return iconMap[type] || 'icon-transaction'
    },
    getTypeText(type) {
      const textMap = {
        1: '充值',
        2: '提现',
        3: '停车收益',
        4: '停车支出'
      }
      return textMap[type] || '交易'
    },
    formatTime(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const month = date.getMonth() + 1
      const day = date.getDate()
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      return `${month}月${day}日 ${hour}:${minute}`
    },
    goRecharge() {
      uni.navigateTo({ url: '/pages/wallet/recharge' })
    },
    goWithdraw() {
      uni.navigateTo({ url: '/pages/wallet/withdraw' })
    },
    goTransactions() {
      uni.navigateTo({ url: '/pages/wallet/transactions' })
    }
  }
}
</script>

<style scoped>
.wallet-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.wallet-card {
  position: relative;
  margin: 20rpx;
  height: 320rpx;
  border-radius: 20rpx;
  overflow: hidden;
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
}

.card-content {
  position: relative;
  z-index: 1;
  padding: 40rpx;
  color: #fff;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.balance-label {
  font-size: 26rpx;
  opacity: 0.9;
}

.balance-row {
  margin-top: 16rpx;
}

.balance-value {
  font-size: 64rpx;
  font-weight: bold;
}

.action-btns {
  display: flex;
  gap: 24rpx;
  margin-top: auto;
}

.wallet-btn {
  flex: 1;
  height: 72rpx;
  line-height: 72rpx;
  text-align: center;
  border-radius: 36rpx;
  font-size: 28rpx;
  font-weight: 500;
  border: none;
}

.wallet-btn.recharge {
  background: rgba(255,255,255,0.3);
  color: #fff;
}

.wallet-btn.withdraw {
  background: #fff;
  color: #007bff;
}

.income-card {
  display: flex;
  background: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx 0;
}

.income-item {
  flex: 1;
  text-align: center;
}

.income-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  display: block;
}

.income-label {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.divider {
  width: 1rpx;
  background: #eee;
}

.section {
  background: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.section-more {
  font-size: 26rpx;
  color: #007bff;
}

.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 28rpx;
}

.transaction-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.transaction-item:last-child {
  border-bottom: none;
}

.trans-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
}

.trans-icon.recharge { background: #e3f2fd; color: #2196f3; }
.trans-icon.withdraw { background: #fff3e0; color: #ff9800; }
.trans-icon.income { background: #e8f5e9; color: #4caf50; }
.trans-icon.expense { background: #ffebee; color: #f44336; }

.trans-info {
  flex: 1;
  margin-left: 20rpx;
}

.trans-title {
  font-size: 28rpx;
  color: #333;
  display: block;
}

.trans-time {
  font-size: 24rpx;
  color: #999;
  margin-top: 4rpx;
}

.trans-amount {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.trans-amount.income {
  color: #4caf50;
}
</style>
