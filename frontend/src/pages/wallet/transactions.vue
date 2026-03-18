<template>
  <view class="transactions-page">
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
      class="transaction-list"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="transactions.length === 0 && !loading" class="empty-state">
        <text>暂无交易记录</text>
      </view>

      <view class="transaction-item" v-for="item in transactions" :key="item.id">
        <view class="trans-info">
          <text class="trans-title">{{ getTypeText(item.type) }}</text>
          <text class="trans-desc">{{ item.description || '-' }}</text>
          <text class="trans-time">{{ formatDateTime(item.createdAt) }}</text>
        </view>
        <view class="trans-amount" :class="item.amount >= 0 ? 'income' : 'expense'">
          {{ item.amount >= 0 ? '+' : '' }}{{ item.amount }}
        </view>
      </view>

      <view class="load-more" v-if="transactions.length > 0">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getTransactions, getTransactionTypeText } from '../../api/wallet.js'

export default {
  data() {
    return {
      tabs: [
        { label: '全部', value: 'all' },
        { label: '支出', value: 1 },
        { label: '收入', value: 2 },
        { label: '提现', value: 3 },
        { label: '退款', value: 4 },
        { label: '充值', value: 5 }
      ],
      currentTab: 'all',
      transactions: [],
      pageNum: 1,
      pageSize: 20,
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
    async loadTransactions(append = false) {
      if (this.loading) return
      this.loading = true
      try {
        const type = this.currentTab === 'all' ? null : this.currentTab
        const res = await getTransactions(type, this.pageNum, this.pageSize)
        if (res.code === 200) {
          const list = res.data.records || res.data.list || res.data || []
          this.transactions = append ? [...this.transactions, ...list] : list
          const total = res.data.total || list.length
          this.noMore = this.transactions.length >= total
        }
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
        this.refreshing = false
      }
    },
    refreshList() {
      this.pageNum = 1
      this.noMore = false
      this.loadTransactions(false)
    },
    loadMore() {
      if (this.loading || this.noMore) return
      this.pageNum++
      this.loadTransactions(true)
    },
    onRefresh() {
      this.refreshing = true
      this.refreshList()
    },
    getTypeText(type) {
      return getTransactionTypeText(type)
    },
    formatDateTime(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const month = date.getMonth() + 1
      const day = date.getDate()
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      return `${month}月${day}日 ${hour}:${minute}`
    }
  }
}
</script>

<style scoped>
.transactions-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.tab-bar {
  display: flex;
  flex-wrap: wrap;
  background: #fff;
  padding: 0 10rpx;
  border-bottom: 1rpx solid #eee;
}

.tab-item {
  flex: 1;
  min-width: 120rpx;
  text-align: center;
  padding: 26rpx 0;
  font-size: 26rpx;
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
  width: 50rpx;
  height: 4rpx;
  background: #007bff;
  border-radius: 2rpx;
}

.transaction-list {
  flex: 1;
  padding: 20rpx;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
}

.trans-info {
  flex: 1;
}

.trans-title {
  font-size: 28rpx;
  color: #333;
  display: block;
}

.trans-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
  display: block;
}

.trans-time {
  font-size: 24rpx;
  color: #aaa;
  margin-top: 6rpx;
  display: block;
}

.trans-amount {
  font-size: 30rpx;
  font-weight: bold;
  margin-left: 20rpx;
}

.trans-amount.income { color: #4caf50; }
.trans-amount.expense { color: #f44336; }

.empty-state {
  text-align: center;
  padding: 120rpx 0;
  color: #999;
  font-size: 28rpx;
}

.load-more {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 26rpx;
}
</style>
