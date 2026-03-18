<template>
  <view class="detail-page">
    <!-- 车位图片轮播 -->
    <swiper class="image-swiper" indicator-dots autoplay circular>
      <swiper-item v-for="(img, index) in images" :key="index">
        <image class="swiper-image" :src="img" mode="aspectFill" @click="previewImage(index)" />
      </swiper-item>
    </swiper>

    <!-- 基本信息 -->
    <view class="info-card">
      <view class="info-header">
        <text class="title">{{ parking.title }}</text>
        <view class="favorite-btn" @click="toggleFavorite">
          <text :class="['iconfont', isFavorite ? 'icon-heart-fill' : 'icon-heart']"></text>
        </view>
      </view>
      
      <view class="price-row">
        <text class="price">¥{{ parking.pricePerHour }}</text>
        <text class="price-unit">/小时</text>
      </view>
      
      <view class="address-row">
        <text class="iconfont icon-location"></text>
        <text class="address">{{ parking.communityName }} · {{ parking.address }}</text>
      </view>
      
      <view class="space-no" v-if="parking.spaceNumber">
        <text class="label">车位编号：</text>
        <text class="value">{{ parking.spaceNumber }}</text>
      </view>
    </view>

    <!-- 描述信息 -->
    <view class="desc-card" v-if="parking.description">
      <text class="card-title">车位描述</text>
      <text class="desc-text">{{ parking.description }}</text>
    </view>

    <!-- 可用时段 -->
    <view class="rules-card">
      <text class="card-title">可用时段</text>
      <view class="rules-list" v-if="rules.length > 0">
        <view class="rule-item" v-for="rule in rules" :key="rule.id">
          <text class="rule-time">{{ formatRuleTime(rule) }}</text>
          <text class="rule-day">{{ formatRuleDay(rule) }}</text>
        </view>
      </view>
      <view class="empty-rules" v-else>
        <text>暂无可用时段</text>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="contact-btn" @click="contactOwner">
        <text class="iconfont icon-phone"></text>
        <text>联系车位主</text>
      </view>
      <button class="reserve-btn" @click="goToReserve" :disabled="parking.status !== 1">
        {{ parking.status === 1 ? '立即预约' : '暂不可预约' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getParkingDetail, getParkingRules } from '../../api/parking.js'
import { addFavorite, removeFavorite, checkFavorite } from '../../api/favorite.js'

export default {
  data() {
    return {
      id: null,
      parking: {},
      images: [],
      rules: [],
      isFavorite: false
    }
  },
  onLoad(options) {
    this.id = options.id
    this.loadData()
  },
  methods: {
    async loadData() {
      uni.showLoading({ title: '加载中' })
      try {
        await Promise.all([
          this.loadDetail(),
          this.loadRules(),
          this.checkFavoriteStatus()
        ])
      } finally {
        uni.hideLoading()
      }
    },
    async loadDetail() {
      const res = await getParkingDetail(this.id)
      if (res.code === 200) {
        this.parking = res.data
        this.parseImages()
      }
    },
    async loadRules() {
      const res = await getParkingRules(this.id)
      if (res.code === 200) {
        this.rules = res.data || []
      }
    },
    async checkFavoriteStatus() {
      try {
        const res = await checkFavorite(this.id)
        if (res.code === 200) {
          this.isFavorite = res.data
        }
      } catch (e) {
        console.error('检查收藏状态失败:', e)
      }
    },
    parseImages() {
      try {
        this.images = JSON.parse(this.parking.images || '[]')
        if (this.images.length === 0) {
          this.images = ['/static/images/default-parking.png']
        }
      } catch {
        this.images = ['/static/images/default-parking.png']
      }
    },
    previewImage(index) {
      uni.previewImage({
        current: index,
        urls: this.images
      })
    },
    async toggleFavorite() {
      try {
        if (this.isFavorite) {
          await removeFavorite(this.id)
          this.isFavorite = false
          uni.showToast({ title: '已取消收藏', icon: 'none' })
        } else {
          await addFavorite(this.id)
          this.isFavorite = true
          uni.showToast({ title: '收藏成功', icon: 'success' })
        }
      } catch (e) {
        uni.showToast({ title: '操作失败', icon: 'none' })
      }
    },
    formatRuleTime(rule) {
      const start = rule.startTime ? rule.startTime.substring(0, 5) : '00:00'
      const end = rule.endTime ? rule.endTime.substring(0, 5) : '24:00'
      return `${start} - ${end}`
    },
    formatRuleDay(rule) {
      if (rule.ruleType === 2 && rule.specificDate) {
        return rule.specificDate
      }
      if (rule.dayOfWeek) {
        const dayMap = { '1': '周一', '2': '周二', '3': '周三', '4': '周四', '5': '周五', '6': '周六', '7': '周日' }
        const days = rule.dayOfWeek.split(',').map(d => dayMap[d] || d)
        return days.join('、')
      }
      return '每天'
    },
    contactOwner() {
      uni.showToast({ title: '暂不支持此功能', icon: 'none' })
    },
    goToReserve() {
      uni.navigateTo({
        url: `/pages/reservation/create?spaceId=${this.id}&price=${this.parking.pricePerHour}`
      })
    }
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 140rpx;
}

.image-swiper {
  width: 100%;
  height: 500rpx;
}

.swiper-image {
  width: 100%;
  height: 100%;
}

.info-card, .desc-card, .rules-card {
  background: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.title {
  flex: 1;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
}

.favorite-btn {
  padding: 10rpx;
}

.favorite-btn .icon-heart-fill {
  color: #ff6b6b;
  font-size: 48rpx;
}

.favorite-btn .icon-heart {
  color: #ccc;
  font-size: 48rpx;
}

.price-row {
  margin-top: 20rpx;
}

.price {
  font-size: 44rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.price-unit {
  font-size: 26rpx;
  color: #999;
}

.address-row {
  display: flex;
  align-items: center;
  margin-top: 20rpx;
  color: #666;
}

.address-row .iconfont {
  font-size: 28rpx;
  margin-right: 8rpx;
  color: #007bff;
}

.address {
  font-size: 28rpx;
}

.space-no {
  margin-top: 20rpx;
  font-size: 26rpx;
}

.space-no .label {
  color: #999;
}

.space-no .value {
  color: #333;
}

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.desc-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}

.rules-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.rule-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 20rpx;
  background: #f8f9fa;
  border-radius: 8rpx;
}

.rule-time {
  font-size: 28rpx;
  color: #007bff;
  font-weight: 500;
}

.rule-day {
  font-size: 24rpx;
  color: #999;
}

.empty-rules {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 28rpx;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.08);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.contact-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 30rpx;
  color: #666;
}

.contact-btn .iconfont {
  font-size: 40rpx;
  margin-bottom: 4rpx;
}

.contact-btn text:last-child {
  font-size: 22rpx;
}

.reserve-btn {
  flex: 1;
  height: 88rpx;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 500;
  border-radius: 44rpx;
  margin-left: 30rpx;
  border: none;
}

.reserve-btn[disabled] {
  background: #ccc;
}
</style>
