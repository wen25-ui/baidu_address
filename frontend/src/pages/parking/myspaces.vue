<template>
  <view class="myspaces-page">
    <view class="stats-card">
      <view class="stat-item">
        <text class="stat-value">{{ stats.total || 0 }}</text>
        <text class="stat-label">总车位</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.online || 0 }}</text>
        <text class="stat-label">已上架</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.offline || 0 }}</text>
        <text class="stat-label">未上架</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ formatMoney(stats.totalIncome) }}</text>
        <text class="stat-label">累计收益</text>
      </view>
    </view>

    <scroll-view
      class="space-list"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="spaces.length === 0 && !loading" class="empty-state">
        <text>暂无车位，先去发布吧</text>
      </view>

      <view class="space-item" v-for="space in spaces" :key="space.id">
        <image class="space-image" :src="getFirstImage(space.images)" mode="aspectFill" />
        <view class="space-info" @click="goDetail(space.id)">
          <text class="space-title">{{ space.title || '未命名车位' }}</text>
          <text class="space-address">{{ space.communityName }} {{ space.address }}</text>
          <view class="space-meta">
            <text class="space-price">¥{{ space.pricePerHour }}/小时</text>
            <text class="space-status" :class="statusClass(space.status)">{{ statusText(space.status) }}</text>
          </view>
        </view>
        <view class="space-actions">
          <button
            class="action-btn"
            v-if="space.status === 1 || space.status === 2"
            @click.stop="toggleStatus(space)"
          >{{ space.status === 1 ? '下架' : '上架' }}</button>
          <button
            class="action-btn ghost"
            @click.stop="goDetail(space.id)"
          >详情</button>
          <button
            class="action-btn danger"
            @click.stop="removeSpace(space)"
          >删除</button>
        </view>
        <view class="reject" v-if="space.status === 3 && space.rejectReason">
          <text>驳回原因：{{ space.rejectReason }}</text>
        </view>
      </view>

      <view class="load-more" v-if="spaces.length > 0">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getMyParkingSpaces, onlineParking, offlineParking, deleteParking, getParkingStats } from '../../api/parking.js'

export default {
  data() {
    return {
      spaces: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      refreshing: false,
      noMore: false,
      stats: {}
    }
  },
  onShow() {
    this.refreshList()
    this.loadStats()
  },
  methods: {
    async loadStats() {
      try {
        const res = await getParkingStats()
        if (res.code === 200) {
          this.stats = res.data || {}
        }
      } catch (e) {
        console.error('加载统计失败:', e)
      }
    },
    async loadSpaces(append = false) {
      if (this.loading) return
      this.loading = true
      try {
        const res = await getMyParkingSpaces(this.pageNum, this.pageSize)
        if (res.code === 200) {
          const list = res.data.records || res.data.list || res.data || []
          this.spaces = append ? [...this.spaces, ...list] : list
          const total = res.data.total || list.length
          this.noMore = this.spaces.length >= total
        }
      } catch (e) {
        console.error('加载车位失败:', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
        this.refreshing = false
      }
    },
    refreshList() {
      this.pageNum = 1
      this.noMore = false
      this.loadSpaces(false)
    },
    loadMore() {
      if (this.loading || this.noMore) return
      this.pageNum++
      this.loadSpaces(true)
    },
    onRefresh() {
      this.refreshing = true
      this.refreshList()
    },
    statusText(status) {
      const map = { 0: '审核中', 1: '已上架', 2: '已下架', 3: '已驳回' }
      return map[status] || '未知'
    },
    statusClass(status) {
      const map = { 0: 'pending', 1: 'online', 2: 'offline', 3: 'reject' }
      return map[status] || ''
    },
    formatMoney(value) {
      if (value === undefined || value === null) return '0.00'
      const num = parseFloat(value)
      if (Number.isNaN(num)) return '0.00'
      return num.toFixed(2)
    },
    getFirstImage(images) {
      if (!images) return '/static/images/default-parking.png'
      try {
        const arr = JSON.parse(images)
        return arr[0] || '/static/images/default-parking.png'
      } catch {
        return '/static/images/default-parking.png'
      }
    },
    toggleStatus(space) {
      const action = space.status === 1 ? '下架' : '上架'
      uni.showModal({
        title: action,
        content: `确定要${action}该车位吗？`,
        success: async (res) => {
          if (!res.confirm) return
          try {
            const api = space.status === 1 ? offlineParking : onlineParking
            const result = await api(space.id)
            if (result.code === 200) {
              uni.showToast({ title: `${action}成功`, icon: 'success' })
              this.refreshList()
              this.loadStats()
            } else {
              uni.showToast({ title: result.message || `${action}失败`, icon: 'none' })
            }
          } catch (e) {
            uni.showToast({ title: `${action}失败`, icon: 'none' })
          }
        }
      })
    },
    removeSpace(space) {
      uni.showModal({
        title: '删除车位',
        content: '删除后将无法恢复，确定删除？',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await deleteParking(space.id)
            if (result.code === 200) {
              uni.showToast({ title: '删除成功', icon: 'success' })
              this.refreshList()
              this.loadStats()
            } else {
              uni.showToast({ title: result.message || '删除失败', icon: 'none' })
            }
          } catch (e) {
            uni.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      })
    },
    goDetail(id) {
      uni.navigateTo({ url: `/pages/parking/detail?id=${id}` })
    }
  }
}
</script>

<style scoped>
.myspaces-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.stats-card {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
}

.stat-item {
  flex: 1;
  min-width: 180rpx;
  text-align: center;
}

.stat-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: block;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
  display: block;
}

.space-list {
  padding: 0 20rpx 40rpx;
}

.space-item {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.space-image {
  width: 100%;
  height: 260rpx;
  border-radius: 12rpx;
}

.space-info {
  margin-top: 16rpx;
}

.space-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  display: block;
}

.space-address {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
}

.space-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10rpx;
}

.space-price {
  font-size: 28rpx;
  color: #ff6b6b;
  font-weight: 500;
}

.space-status {
  font-size: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

.space-status.pending { background: #fff3cd; color: #ff9800; }
.space-status.online { background: #e8f5e9; color: #4caf50; }
.space-status.offline { background: #f5f5f5; color: #666; }
.space-status.reject { background: #ffebee; color: #f44336; }

.space-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 16rpx;
}

.action-btn {
  flex: 1;
  height: 64rpx;
  line-height: 64rpx;
  text-align: center;
  border-radius: 32rpx;
  border: none;
  background: #007bff;
  color: #fff;
  font-size: 24rpx;
}

.action-btn.ghost {
  background: #f5f5f5;
  color: #666;
}

.action-btn.danger {
  background: #f44336;
  color: #fff;
}

.reject {
  margin-top: 12rpx;
  padding: 12rpx 16rpx;
  background: #fff7f7;
  border-radius: 10rpx;
  color: #f44336;
  font-size: 24rpx;
}

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
