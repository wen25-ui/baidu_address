<template>
  <view class="credit-page">
    <view class="score-card">
      <text class="score-title">当前信用分</text>
      <text class="score-value">{{ score }}</text>
      <view class="score-bar">
        <view class="score-progress" :style="{ width: progress + '%' }"></view>
      </view>
      <text class="score-tip">信用良好将更容易预约车位</text>
    </view>

    <view class="record-card">
      <view class="card-header">
        <text class="card-title">信用记录</text>
        <text class="card-subtitle">最近变动</text>
      </view>
      <view v-if="records.length === 0 && !loading" class="empty-state">
        <text>暂无信用记录</text>
      </view>
      <view class="record-item" v-for="record in records" :key="record.id">
        <view class="record-info">
          <text class="record-title">{{ getTypeText(record.changeType) }}</text>
          <text class="record-time">{{ formatDateTime(record.createdAt) }}</text>
        </view>
        <view class="record-score">
          <text :class="['score-change', record.changeValue >= 0 ? 'positive' : 'negative']">
            {{ record.changeValue >= 0 ? '+' : '' }}{{ record.changeValue }}
          </text>
          <text class="score-after">{{ record.afterScore }}</text>
        </view>
      </view>

      <view class="load-more" v-if="records.length > 0">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getCreditScore, getCreditRecords, getChangeTypeText } from '../../api/credit.js'

export default {
  data() {
    return {
      score: 0,
      records: [],
      pageNum: 1,
      pageSize: 20,
      loading: false,
      noMore: false
    }
  },
  computed: {
    progress() {
      const value = Math.max(0, Math.min(100, this.score))
      return value
    }
  },
  onShow() {
    this.loadScore()
    this.refreshRecords()
  },
  methods: {
    async loadScore() {
      try {
        const res = await getCreditScore()
        if (res.code === 200) {
          this.score = res.data || 0
        }
      } catch (e) {
        console.error('加载信用分失败:', e)
      }
    },
    async loadRecords(append = false) {
      if (this.loading) return
      this.loading = true
      try {
        const res = await getCreditRecords(this.pageNum, this.pageSize)
        if (res.code === 200) {
          const list = res.data.records || res.data.list || res.data || []
          this.records = append ? [...this.records, ...list] : list
          const total = res.data.total || list.length
          this.noMore = this.records.length >= total
        }
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    refreshRecords() {
      this.pageNum = 1
      this.noMore = false
      this.loadRecords(false)
    },
    getTypeText(type) {
      return getChangeTypeText(type)
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
.credit-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.score-card {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  text-align: center;
  margin-bottom: 20rpx;
}

.score-title {
  font-size: 28rpx;
  opacity: 0.9;
  display: block;
}

.score-value {
  font-size: 72rpx;
  font-weight: bold;
  margin: 20rpx 0;
  display: block;
}

.score-bar {
  width: 100%;
  height: 16rpx;
  background: rgba(255,255,255,0.3);
  border-radius: 8rpx;
  overflow: hidden;
}

.score-progress {
  height: 100%;
  background: #fff;
  border-radius: 8rpx;
}

.score-tip {
  font-size: 24rpx;
  opacity: 0.8;
  margin-top: 16rpx;
  display: block;
}

.record-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.card-subtitle {
  font-size: 24rpx;
  color: #999;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.record-item:last-child {
  border-bottom: none;
}

.record-title {
  font-size: 28rpx;
  color: #333;
  display: block;
}

.record-time {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
  display: block;
}

.record-score {
  text-align: right;
}

.score-change {
  font-size: 28rpx;
  font-weight: bold;
  display: block;
}

.score-change.positive { color: #4caf50; }
.score-change.negative { color: #f44336; }

.score-after {
  font-size: 24rpx;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 80rpx 0;
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
