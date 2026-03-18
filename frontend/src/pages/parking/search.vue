<template>
  <view class="search-page">
    <view class="search-bar">
      <input
        class="search-input"
        v-model="keyword"
        placeholder="输入关键词搜索车位"
        @confirm="doSearch"
      />
      <button class="search-btn" @click="doSearch">搜索</button>
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
        <text>暂无搜索结果</text>
      </view>

      <view class="space-item" v-for="space in spaces" :key="space.id" @click="goDetail(space.id)">
        <image class="space-image" :src="getFirstImage(space.images)" mode="aspectFill" />
        <view class="space-info">
          <text class="space-title">{{ space.title || '车位' }}</text>
          <text class="space-address">{{ space.communityName }} {{ space.address }}</text>
          <view class="space-meta">
            <text class="space-price">¥{{ space.pricePerHour }}/小时</text>
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
import { searchParking } from '../../api/parking.js'

export default {
  data() {
    return {
      keyword: '',
      spaces: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      refreshing: false,
      noMore: false
    }
  },
  onLoad(options) {
    if (options.keyword) {
      this.keyword = options.keyword
      this.refreshList()
    }
  },
  methods: {
    async loadSpaces(append = false) {
      if (this.loading) return
      this.loading = true
      try {
        const res = await searchParking(this.keyword, this.pageNum, this.pageSize)
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
    doSearch() {
      this.refreshList()
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
    goDetail(id) {
      uni.navigateTo({ url: `/pages/parking/detail?id=${id}` })
    }
  }
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.search-bar {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}

.search-input {
  flex: 1;
  height: 72rpx;
  background: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
}

.search-btn {
  margin-left: 16rpx;
  height: 72rpx;
  line-height: 72rpx;
  padding: 0 30rpx;
  border-radius: 36rpx;
  background: #007bff;
  color: #fff;
  font-size: 28rpx;
  border: none;
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
