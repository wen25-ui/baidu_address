<template>
  <view class="index-page">
    <map
      id="parkingMap"
      class="map-view"
      :latitude="latitude"
      :longitude="longitude"
      :scale="mapScale"
      :markers="markers"
      show-location
      @markertap="onMarkerTap"
    />

    <view class="search-bar">
      <view class="search-input" @click="goToSearch">
        <text class="placeholder">搜索附近车位...</text>
      </view>
      <view class="location" @click="getCurrentLocation">
        <text class="location-text">{{ locationText }}</text>
      </view>
    </view>

    <view class="map-controls">
      <button class="control-btn" @click="resetLocation">复位</button>
      <button class="control-btn" @click="toggleCoords">坐标</button>
      <button class="control-btn" :disabled="!selectedDestination" @click="navigateToDestination">导航</button>
    </view>

    <view class="coords-panel" v-if="showCoords">
      <text>Lat: {{ latitude }}</text>
      <text>Lng: {{ longitude }}</text>
    </view>

    <view class="sheet" :class="{ expanded: listExpanded }">
      <view class="sheet-handle" @click="toggleList">
        <view class="handle-bar"></view>
        <view class="sheet-title-wrap">
          <text class="sheet-title">附近车位</text>
          <text class="sheet-count">({{ parkingList.length }})</text>
        </view>
        <text class="sheet-action">{{ listExpanded ? '收起' : '展开' }}</text>
      </view>

      <view class="quick-entry">
        <view class="entry-item" @click="toggleList">
          <view class="entry-icon nearby"><text class="entry-glyph">附</text></view>
          <text class="entry-text">附近车位</text>
        </view>
        <view class="entry-item" @click="goToPublish">
          <view class="entry-icon publish"><text class="entry-glyph">发</text></view>
          <text class="entry-text">发布车位</text>
        </view>
        <view class="entry-item" @click="goToMySpaces">
          <view class="entry-icon myspace"><text class="entry-glyph">我</text></view>
          <text class="entry-text">我的车位</text>
        </view>
        <view class="entry-item" @click="goToFavorite">
          <view class="entry-icon favorite"><text class="entry-glyph">藏</text></view>
          <text class="entry-text">我的收藏</text>
        </view>
      </view>

      <view class="search-mode-tabs">
        <view
          class="mode-tab"
          :class="{ active: searchMode === 'destination' }"
          @click="switchSearchMode('destination')"
        >
          目的地推荐
        </view>
        <view
          class="mode-tab"
          :class="{ active: searchMode === 'parking' }"
          @click="switchSearchMode('parking')"
        >
          指定停车场
        </view>
      </view>

      <view class="dest-search">
        <input
          class="dest-input"
          v-model="searchKeyword"
          :placeholder="searchMode === 'destination' ? '搜索目的地，自动推荐附近车位' : '输入停车场/地名关键词'"
          @confirm="handleSearch"
        />
        <button class="dest-btn" @click="handleSearch" :loading="searching">搜索</button>
      </view>

      <scroll-view class="sheet-list" scroll-y v-if="listExpanded">
        <view class="section-title" v-if="searchMode === 'destination' && poiResults.length > 0">目的地搜索结果</view>
        <view class="poi-item" v-for="poi in poiResults" :key="poi.uid" @click="selectPoi(poi)">
          <view class="poi-info">
            <text class="poi-name">{{ poi.name }}</text>
            <text class="poi-address">{{ poi.address || '-' }}</text>
          </view>
          <button class="poi-nav" @click.stop="navigateToPoi(poi)">导航</button>
        </view>

        <view class="section-title" v-if="searchMode === 'destination' && recommendedParkingList.length > 0">
          推荐车位（目的地附近）
        </view>
        <view class="parking-item" v-for="item in recommendedParkingList" :key="`rec-${item.id}`">
          <image class="parking-image" :src="getFirstImage(item.images)" mode="aspectFill" @click="goToDetail(item.id)" />
          <view class="parking-info" @click="goToDetail(item.id)">
            <text class="parking-title">{{ item.title }}</text>
            <text class="parking-address">{{ item.communityName }} {{ item.address }}</text>
            <view class="parking-bottom">
              <text class="parking-price">
                <text class="price-value">¥{{ item.pricePerHour }}</text>
                <text class="price-unit">/小时</text>
              </text>
              <text class="parking-distance" v-if="item.distance">{{ formatDistance(item.distance) }}</text>
            </view>
          </view>
          <button class="parking-nav" @click.stop="navigateToParking(item)">导航</button>
        </view>

        <view class="section-title" v-if="searchMode === 'parking'">停车场搜索结果</view>
        <view v-if="searchMode === 'parking' && parkingSearchResults.length === 0 && searchKeyword && !searching" class="empty-state">
          <text class="empty-text">未找到匹配停车场</text>
          <text class="empty-hint">请尝试更换关键词</text>
        </view>
        <view class="parking-item" v-for="item in parkingSearchResults" :key="`search-${item.id}`">
          <image class="parking-image" :src="getFirstImage(item.images)" mode="aspectFill" @click="goToDetail(item.id)" />
          <view class="parking-info" @click="goToDetail(item.id)">
            <text class="parking-title">{{ item.title }}</text>
            <text class="parking-address">{{ item.communityName }} {{ item.address }}</text>
            <view class="parking-bottom">
              <text class="parking-price">
                <text class="price-value">¥{{ item.pricePerHour }}</text>
                <text class="price-unit">/小时</text>
              </text>
            </view>
          </view>
          <button class="parking-nav" @click.stop="navigateToParking(item)">导航</button>
        </view>

        <view class="section-header">
          <text class="section-title">当前位置附近车位</text>
          <button class="refresh-btn" :loading="loading" @click="refreshNearby">刷新</button>
        </view>
        <view v-if="parkingList.length === 0 && !loading" class="empty-state">
          <text class="empty-text">暂无附近车位</text>
          <text class="empty-hint">试试扩大搜索范围</text>
        </view>

        <view class="parking-item" v-for="item in parkingList" :key="item.id">
          <image class="parking-image" :src="getFirstImage(item.images)" mode="aspectFill" @click="goToDetail(item.id)" />
          <view class="parking-info" @click="goToDetail(item.id)">
            <text class="parking-title">{{ item.title }}</text>
            <text class="parking-address">{{ item.communityName }} {{ item.address }}</text>
            <view class="parking-bottom">
              <text class="parking-price">
                <text class="price-value">¥{{ item.pricePerHour }}</text>
                <text class="price-unit">/小时</text>
              </text>
              <text class="parking-distance" v-if="item.distance">{{ formatDistance(item.distance) }}</text>
            </view>
          </view>
          <button class="parking-nav" @click.stop="navigateToParking(item)">导航</button>
        </view>

        <view class="loading-state" v-if="loading">
          <text class="loading-text">加载中...</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import { searchNearby, searchParking } from '../../api/parking.js'
import { getToken } from '../../utils/auth.js'

const bmap = require('../../libs/bmap-wx.min.js')
const BAIDU_AK_MP = 'ZmaKnx0vJcB745El7rFTFdqadqxM5JXt'

export default {
  data() {
    return {
      locationText: '定位中...',
      latitude: 0,
      longitude: 0,
      mapScale: 14,
      parkingList: [],
      loading: false,
      listExpanded: false,
      showCoords: false,
      searchMode: 'destination',
      searchKeyword: '',
      poiResults: [],
      parkingSearchResults: [],
      recommendedParkingList: [],
      searching: false,
      selectedDestination: null
    }
  },
  computed: {
    markers() {
      const allParking = [...this.parkingList, ...this.recommendedParkingList, ...this.parkingSearchResults]
      const uniqueParking = []
      const seenIds = new Set()

      allParking.forEach((item) => {
        if (!item || !item.id || !item.latitude || !item.longitude) return
        if (seenIds.has(Number(item.id))) return
        seenIds.add(Number(item.id))
        uniqueParking.push(item)
      })

      const parkingMarkers = uniqueParking.map((item) => ({
        id: Number(item.id),
        latitude: Number(item.latitude),
        longitude: Number(item.longitude),
        width: 32,
        height: 32,
        iconPath: '/static/images/marker.png',
        callout: {
          content: item.title || '车位',
          display: 'ALWAYS',
          padding: 6,
          borderRadius: 6,
          bgColor: '#ffffff',
          color: '#333333',
          fontSize: 12
        }
      }))

      const destMarker = this.selectedDestination
        ? [{
            id: 999999,
            latitude: Number(this.selectedDestination.latitude),
            longitude: Number(this.selectedDestination.longitude),
            width: 36,
            height: 36,
            iconPath: '/static/images/marker.png',
            callout: {
              content: this.selectedDestination.name || '目的地',
              display: 'ALWAYS',
              padding: 6,
              borderRadius: 6,
              bgColor: '#fff7e6',
              color: '#ff7a00',
              fontSize: 12
            }
          }]
        : []

      return [...parkingMarkers, ...destMarker]
    }
  },
  onLoad() {
    if (!this.ensureLoggedIn()) {
      return
    }
    this.bmap = new bmap.BMapWX({ ak: BAIDU_AK_MP })
  },
  onReady() {
    this.mapCtx = uni.createMapContext('parkingMap', this)
  },
  onShow() {
    if (!this.ensureLoggedIn()) {
      return
    }
    this.getCurrentLocation()
  },
  methods: {
    ensureLoggedIn() {
      if (getToken()) {
        return true
      }
      uni.reLaunch({ url: '/pages/login/login' })
      return false
    },
    getCurrentLocation() {
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.latitude = res.latitude
          this.longitude = res.longitude
          this.locationText = '当前定位'
          this.loadNearbyParking()
        },
        fail: () => {
          this.locationText = '定位失败'
          uni.showToast({ title: '获取定位失败', icon: 'none' })
        }
      })
    },
    resetLocation() {
      if (this.mapCtx) {
        this.mapCtx.moveToLocation()
      }
      this.getCurrentLocation()
    },
    toggleCoords() {
      this.showCoords = !this.showCoords
    },
    refreshNearby() {
      if (this.loading) return
      if (!this.ensureLoggedIn()) {
        return
      }
      if (this.latitude && this.longitude) {
        this.loadNearbyParking()
        return
      }
      this.getCurrentLocation()
    },
    async loadNearbyParking() {
      if (!this.latitude || !this.longitude) return

      this.loading = true
      try {
        const res = await searchNearby(this.latitude, this.longitude, 5, 1, 20)
        if (res.code === 200) {
          const records = res.data.records || res.data || []
          this.parkingList = this.normalizeParkingList(records, {
            latitude: this.latitude,
            longitude: this.longitude
          })
        }
      } catch (error) {
        console.error('加载车位失败:', error)
      } finally {
        this.loading = false
      }
    },
    switchSearchMode(mode) {
      if (this.searchMode === mode) return
      this.searchMode = mode
      this.searchKeyword = ''
      this.poiResults = []
      this.selectedDestination = null
      this.recommendedParkingList = []
      this.parkingSearchResults = []
    },
    handleSearch() {
      if (this.searchMode === 'destination') {
        this.searchDestination()
      } else {
        this.searchParkingLot()
      }
    },
    searchDestination() {
      if (!this.searchKeyword) return

      this.searching = true
      this.poiResults = []
      this.recommendedParkingList = []

      this.bmap.search({
        query: this.searchKeyword,
        location: `${this.latitude},${this.longitude}`,
        success: (data) => {
          const results = data?.originalData?.results || []
          this.poiResults = results.map((item) => ({
            uid: item.uid || `${item.name}-${item.location?.lat}-${item.location?.lng}`,
            name: item.name,
            address: item.address,
            latitude: item.location?.lat,
            longitude: item.location?.lng
          }))
        },
        fail: () => {
          uni.showToast({ title: '搜索失败', icon: 'none' })
        },
        complete: () => {
          this.searching = false
        }
      })
    },
    async searchParkingLot() {
      if (!this.searchKeyword) return

      this.searching = true
      this.parkingSearchResults = []
      try {
        const res = await searchParking(this.searchKeyword, 1, 20)
        if (res.code === 200) {
          const records = res.data.records || res.data || []
          this.parkingSearchResults = this.normalizeParkingList(records, {
            latitude: this.latitude,
            longitude: this.longitude
          })
          if (this.parkingSearchResults.length > 0) {
            const first = this.parkingSearchResults[0]
            if (this.mapCtx && first.latitude && first.longitude) {
              this.mapCtx.includePoints({
                padding: [80, 80, 80, 80],
                points: this.parkingSearchResults
                  .filter(item => item.latitude && item.longitude)
                  .slice(0, 8)
                  .map(item => ({ latitude: Number(item.latitude), longitude: Number(item.longitude) }))
              })
            }
          }
        }
      } catch (error) {
        uni.showToast({ title: '搜索停车场失败', icon: 'none' })
      } finally {
        this.searching = false
      }
    },
    async selectPoi(poi) {
      if (!poi || !poi.latitude || !poi.longitude) return

      this.selectedDestination = poi
      this.recommendedParkingList = []
      if (this.mapCtx) {
        this.mapCtx.includePoints({
          padding: [80, 80, 80, 80],
          points: [
            { latitude: this.latitude, longitude: this.longitude },
            { latitude: Number(poi.latitude), longitude: Number(poi.longitude) }
          ]
        })
      }

      await this.recommendParkingNearDestination(poi)
    },
    async recommendParkingNearDestination(poi) {
      try {
        const res = await searchNearby(Number(poi.latitude), Number(poi.longitude), 3, 1, 20)
        if (res.code === 200) {
          const records = res.data.records || res.data || []
          this.recommendedParkingList = this.normalizeParkingList(records, {
            latitude: Number(poi.latitude),
            longitude: Number(poi.longitude)
          })
          if (this.recommendedParkingList.length === 0) {
            uni.showToast({ title: '目的地附近暂无车位', icon: 'none' })
          }
        }
      } catch (error) {
        uni.showToast({ title: '推荐车位加载失败', icon: 'none' })
      }
    },
    normalizeParkingList(list, origin) {
      if (!Array.isArray(list)) return []
      const originLat = Number(origin?.latitude)
      const originLng = Number(origin?.longitude)

      return list
        .map((item) => {
          const lat = Number(item.latitude)
          const lng = Number(item.longitude)
          if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
            return item
          }

          if (!Number.isFinite(originLat) || !Number.isFinite(originLng)) {
            return item
          }

          const distance = this.calculateDistance(originLat, originLng, lat, lng)
          return {
            ...item,
            distance
          }
        })
        .sort((a, b) => {
          const aDistance = Number.isFinite(Number(a.distance)) ? Number(a.distance) : Number.MAX_SAFE_INTEGER
          const bDistance = Number.isFinite(Number(b.distance)) ? Number(b.distance) : Number.MAX_SAFE_INTEGER
          if (aDistance !== bDistance) {
            return aDistance - bDistance
          }
          const aPrice = Number(a.pricePerHour) || 0
          const bPrice = Number(b.pricePerHour) || 0
          return aPrice - bPrice
        })
    },
    calculateDistance(lat1, lng1, lat2, lng2) {
      const toRadians = (deg) => (deg * Math.PI) / 180
      const earthRadiusKm = 6371
      const dLat = toRadians(lat2 - lat1)
      const dLng = toRadians(lng2 - lng1)
      const a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
        + Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2))
        * Math.sin(dLng / 2) * Math.sin(dLng / 2)
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
      return earthRadiusKm * c
    },
    navigateToDestination() {
      if (!this.selectedDestination) return
      this.navigateToPoi(this.selectedDestination)
    },
    navigateToPoi(poi) {
      if (!poi || !poi.latitude || !poi.longitude) return

      wx.openLocation({
        latitude: Number(poi.latitude),
        longitude: Number(poi.longitude),
        name: poi.name || '目的地',
        address: poi.address || ''
      })
    },
    navigateToParking(item) {
      if (!item || !item.latitude || !item.longitude) return

      wx.openLocation({
        latitude: Number(item.latitude),
        longitude: Number(item.longitude),
        name: item.title || '车位',
        address: `${item.communityName || ''}${item.address || ''}`
      })
    },
    getFirstImage(images) {
      if (!images) return '/static/images/default-parking.png'
      try {
        const list = JSON.parse(images)
        return list[0] || '/static/images/default-parking.png'
      } catch {
        return '/static/images/default-parking.png'
      }
    },
    formatDistance(distance) {
      if (distance < 1) {
        return `${Math.round(distance * 1000)}m`
      }
      return `${distance.toFixed(1)}km`
    },
    goToSearch() {
      uni.navigateTo({ url: '/pages/parking/search' })
    },
    goToPublish() {
      uni.navigateTo({ url: '/pages/parking/publish' })
    },
    goToMySpaces() {
      uni.navigateTo({ url: '/pages/parking/myspaces' })
    },
    goToFavorite() {
      uni.navigateTo({ url: '/pages/favorite/favorite' })
    },
    goToDetail(id) {
      uni.navigateTo({ url: `/pages/parking/detail?id=${id}` })
    },
    toggleList() {
      this.listExpanded = !this.listExpanded
    },
    onMarkerTap(event) {
      const id = Number(event.detail.markerId)
      if (id === 999999) return
      if (id > 0) {
        this.goToDetail(id)
      }
    }
  }
}
</script>

<style scoped>
.index-page {
  position: relative;
  height: 100vh;
  background: #f5f5f5;
  overflow: hidden;
}

.map-view {
  width: 100%;
  height: 100%;
}

.search-bar {
  position: absolute;
  top: 20rpx;
  left: 20rpx;
  right: 20rpx;
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  background: rgba(0, 123, 255, 0.9);
  border-radius: 12rpx;
  z-index: 10;
}

.search-input {
  flex: 1;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 40rpx;
  padding: 12rpx 20rpx;
}

.placeholder {
  color: #999;
  font-size: 26rpx;
}

.location {
  display: flex;
  align-items: center;
  margin-left: 16rpx;
  color: #fff;
}

.location-text {
  font-size: 24rpx;
  max-width: 160rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.map-controls {
  position: absolute;
  right: 20rpx;
  top: 160rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  z-index: 10;
}

.control-btn {
  width: 92rpx;
  height: 60rpx;
  border-radius: 12rpx;
  font-size: 24rpx;
  background: rgba(255, 255, 255, 0.92);
  color: #007bff;
  border: none;
}

.control-btn[disabled] {
  color: #aaa;
}

.coords-panel {
  position: absolute;
  left: 20rpx;
  top: 160rpx;
  background: rgba(0, 0, 0, 0.62);
  color: #fff;
  padding: 12rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  z-index: 10;
}

.sheet {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  box-shadow: 0 -6rpx 20rpx rgba(0, 0, 0, 0.08);
  transform: translateY(70%);
  transition: transform 0.25s ease;
  z-index: 9;
}

.sheet.expanded {
  transform: translateY(0);
}

.sheet-handle {
  position: relative;
  padding: 16rpx 20rpx 10rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.handle-bar {
  width: 80rpx;
  height: 8rpx;
  background: #e0e0e0;
  border-radius: 4rpx;
  margin: 0 auto;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 12rpx;
}

.sheet-title-wrap {
  display: flex;
  align-items: center;
}

.sheet-title {
  font-size: 28rpx;
  color: #333;
  font-weight: 700;
}

.sheet-count {
  margin-left: 6rpx;
  font-size: 24rpx;
  color: #999;
}

.sheet-action {
  font-size: 24rpx;
  color: #007bff;
}

.quick-entry {
  display: flex;
  justify-content: space-around;
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.entry-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}

.entry-icon.nearby { background: linear-gradient(135deg, #3f8cff 0%, #2bc7ff 100%); }
.entry-icon.publish { background: linear-gradient(135deg, #ff7b54 0%, #ffb347 100%); }
.entry-icon.myspace { background: linear-gradient(135deg, #44d7b6 0%, #36b0ff 100%); }
.entry-icon.favorite { background: linear-gradient(135deg, #ff5d88 0%, #ff8f6b 100%); }

.entry-glyph {
  color: #fff;
  font-size: 34rpx;
  font-weight: 700;
}

.entry-text {
  font-size: 24rpx;
  color: #333;
}

.search-mode-tabs {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 0 20rpx 16rpx;
}

.mode-tab {
  flex: 1;
  text-align: center;
  height: 56rpx;
  line-height: 56rpx;
  border-radius: 28rpx;
  font-size: 24rpx;
  color: #5b6b82;
  background: #f2f5fa;
}

.mode-tab.active {
  color: #ffffff;
  background: #007bff;
}

.dest-search {
  display: flex;
  align-items: center;
  padding: 0 20rpx 20rpx;
  gap: 12rpx;
}

.dest-input {
  flex: 1;
  height: 72rpx;
  border-radius: 36rpx;
  background: #f5f5f5;
  padding: 0 24rpx;
  font-size: 26rpx;
}

.dest-btn {
  height: 72rpx;
  line-height: 72rpx;
  padding: 0 24rpx;
  border-radius: 36rpx;
  background: #007bff;
  color: #fff;
  font-size: 26rpx;
  border: none;
}

.sheet-list {
  max-height: 55vh;
  padding: 0 20rpx 20rpx;
}

.section-title {
  font-size: 26rpx;
  color: #666;
  margin: 12rpx 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 12rpx 0;
}

.section-header .section-title {
  margin: 0;
}

.refresh-btn {
  height: 50rpx;
  line-height: 50rpx;
  padding: 0 18rpx;
  border-radius: 26rpx;
  background: #eef5ff;
  color: #007bff;
  font-size: 22rpx;
  border: none;
}

.poi-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx;
  background: #f9f9f9;
  border-radius: 12rpx;
  margin-bottom: 12rpx;
}

.poi-info {
  flex: 1;
}

.poi-name {
  font-size: 26rpx;
  color: #333;
  display: block;
}

.poi-address {
  font-size: 22rpx;
  color: #999;
  margin-top: 4rpx;
  display: block;
}

.poi-nav {
  height: 56rpx;
  line-height: 56rpx;
  padding: 0 20rpx;
  border-radius: 28rpx;
  background: #007bff;
  color: #fff;
  font-size: 22rpx;
  border: none;
}

.parking-item {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  margin-bottom: 20rpx;
}

.parking-image {
  width: 200rpx;
  height: 160rpx;
  flex-shrink: 0;
}

.parking-info {
  flex: 1;
  padding: 16rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.parking-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.parking-address {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.parking-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10rpx;
}

.price-value {
  font-size: 30rpx;
  font-weight: 700;
  color: #ff6b6b;
}

.price-unit {
  font-size: 22rpx;
  color: #999;
}

.parking-distance {
  font-size: 24rpx;
  color: #007bff;
}

.parking-nav {
  align-self: center;
  margin-right: 16rpx;
  height: 60rpx;
  line-height: 60rpx;
  padding: 0 18rpx;
  border-radius: 30rpx;
  background: #007bff;
  color: #fff;
  font-size: 22rpx;
  border: none;
}

.empty-state,
.loading-state {
  padding: 80rpx 0;
  text-align: center;
}

.empty-text,
.loading-text {
  font-size: 26rpx;
  color: #999;
  display: block;
}

.empty-hint {
  font-size: 22rpx;
  color: #ccc;
  margin-top: 10rpx;
  display: block;
}
</style>

