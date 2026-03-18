<template>
  <view class="nearby-page">
    <view class="header">
      <text class="title">附近车位</text>
      <view class="location" @click="getCurrentLocation">
        <text class="location-text">{{ locationText }}</text>
      </view>
    </view>

    <scroll-view
      class="list"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="spaces.length === 0 && !loading" class="empty-state">
        <text>暂无附近车位</text>
      </view>

      <view class="space-item" v-for="space in spaces" :key="space.id" @click="goDetail(space.id)">
        <image class="space-image" :src="getFirstImage(space.images)" mode="aspectFill" />
        <view class="space-info">
          <text class="space-title">{{ space.title || '车位' }}</text>
          <text class="space-address">{{ space.communityName }} {{ space.address }}</text>
          <view class="space-meta">
            <text class="space-price">¥{{ space.pricePerHour }}/小时</text>
            <text class="space-distance" v-if="space.distance">{{ formatDistance(space.distance) }}</text>
          </view>
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
import { searchNearby } from '../../api/parking.js'

export default {
  data() {
    return {
      latitude: null,
      longitude: null,
      locationText: '定位中...',
      spaces: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      refreshing: false,
      noMore: false
    }
  },
  onLoad(options) {
    if (options.lat && options.lng) {
      this.latitude = parseFloat(options.lat)
      this.longitude = parseFloat(options.lng)
      this.locationText = '已获取位置'
      this.refreshList()
    } else {
      this.getCurrentLocation()
    }
  },
  methods: {
    getCurrentLocation() {
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.latitude = res.latitude
          this.longitude = res.longitude
          this.locationText = '当前定位'
          this.refreshList()
        },
        fail: () => {
          this.locationText = '定位失败'
          uni.showToast({ title: '获取定位失败', icon: 'none' })
        }
      })
    },
    async loadSpaces(append = false) {
      if (this.loading || !this.latitude || !this.longitude) return
      this.loading = true
      try {
        const res = await searchNearby(this.latitude, this.longitude, 5, this.pageNum, this.pageSize)
        if (res.code === 200) {
          const list = res.data.records || res.data.list || res.data || []
          this.spaces = append ? [...this.spaces, ...list] : list
          const total = res.data.total || list.length
          this.noMore = this.spaces.length >= total
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
    getFirstImage(images) {
      if (!images) return '/static/images/default-parking.png'
      try {
        const arr = JSON.parse(images)
        return arr[0] || '/static/images/default-parking.png'
      } catch {
        return '/static/images/default-parking.png'
      }
    },
    formatDistance(distance) {
      if (distance < 1) {
        return Math.round(distance * 1000) + 'm'
      }
      return distance.toFixed(1) + 'km'
    },
    goDetail(id) {
      uni.navigateTo({ url: `/pages/parking/detail?id=${id}` })
    }
  }
}
</script>

<style scoped>
.nearby-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 20rpx 30rpx;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #eee;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.location-text {
  font-size: 24rpx;
  color: #007bff;
}

.list {
  flex: 1;
  padding: 20rpx;
}

.space-item {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.space-image {
  width: 100%;
  height: 240rpx;
}

.space-info {
  padding: 20rpx;
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
  margin-top: 12rpx;
}

.space-price {
  font-size: 28rpx;
  color: #ff6b6b;
  font-weight: 500;
}

.space-distance {
  font-size: 24rpx;
  color: #007bff;
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
