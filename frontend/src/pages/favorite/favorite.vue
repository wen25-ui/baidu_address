<template>
  <view class="favorite-page">
    <scroll-view
      class="favorite-list"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="favorites.length === 0 && !loading" class="empty-state">
        <text>暂无收藏</text>
      </view>

      <view class="favorite-item" v-for="item in favorites" :key="item.id">
        <image class="item-image" :src="getFirstImage(item.images)" mode="aspectFill" @click="goDetail(item.id)" />
        <view class="item-info" @click="goDetail(item.id)">
          <text class="item-title">{{ item.title || '车位' }}</text>
          <text class="item-address">{{ item.communityName }} {{ item.address }}</text>
          <view class="item-meta">
            <text class="item-price">¥{{ item.pricePerHour }}/小时</text>
            <text class="item-distance" v-if="item.distance">{{ formatDistance(item.distance) }}</text>
          </view>
        </view>
        <button class="remove-btn" @click.stop="remove(item.id)">取消收藏</button>
      </view>

      <view class="load-more" v-if="favorites.length > 0">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getFavorites, removeFavorite } from '../../api/favorite.js'

export default {
  data() {
    return {
      favorites: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      refreshing: false,
      noMore: false
    }
  },
  onShow() {
    this.refreshList()
  },
  methods: {
    async loadFavorites(append = false) {
      if (this.loading) return
      this.loading = true
      try {
        const res = await getFavorites(this.pageNum, this.pageSize)
        if (res.code === 200) {
          const list = res.data.records || res.data.list || res.data || []
          this.favorites = append ? [...this.favorites, ...list] : list
          const total = res.data.total || list.length
          this.noMore = this.favorites.length >= total
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
      this.loadFavorites(false)
    },
    loadMore() {
      if (this.loading || this.noMore) return
      this.pageNum++
      this.loadFavorites(true)
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
    remove(spaceId) {
      uni.showModal({
        title: '取消收藏',
        content: '确定要取消收藏吗？',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await removeFavorite(spaceId)
            if (result.code === 200) {
              uni.showToast({ title: '已取消收藏', icon: 'success' })
              this.refreshList()
            } else {
              uni.showToast({ title: result.message || '操作失败', icon: 'none' })
            }
          } catch (e) {
            uni.showToast({ title: '操作失败', icon: 'none' })
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
.favorite-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.favorite-list {
  padding: 20rpx;
}

.favorite-item {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.item-image {
  width: 100%;
  height: 240rpx;
}

.item-info {
  padding: 20rpx;
}

.item-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  display: block;
}

.item-address {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12rpx;
}

.item-price {
  font-size: 28rpx;
  color: #ff6b6b;
  font-weight: 500;
}

.item-distance {
  font-size: 24rpx;
  color: #007bff;
}

.remove-btn {
  margin: 0 20rpx 20rpx;
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 32rpx;
  border: none;
  background: #f5f5f5;
  color: #666;
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
