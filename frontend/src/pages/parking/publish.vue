<template>
  <view class="publish-page">
    <view class="card">
      <text class="card-title">基本信息</text>
      <view class="form-item">
        <text class="label">车位标题</text>
        <input class="input" v-model="form.title" placeholder="请输入车位标题" />
      </view>
      <view class="form-row">
        <view class="form-item half">
          <text class="label">价格(元/小时)</text>
          <input class="input" type="number" v-model="form.pricePerHour" placeholder="例如 6.00" />
        </view>
        <view class="form-item half">
          <text class="label">车位编号</text>
          <input class="input" v-model="form.spaceNumber" placeholder="如 A-102" />
        </view>
      </view>
    </view>

    <view class="card">
      <text class="card-title">位置</text>
      <view class="form-item">
        <text class="label">小区/地点</text>
        <input class="input" v-model="form.communityName" placeholder="请输入小区或地点" />
      </view>
      <view class="form-item">
        <text class="label">详细地址</text>
        <input class="input" v-model="form.address" placeholder="请输入详细地址" />
      </view>
      <view class="location-row">
        <view class="location-info">
          <text class="location-text">定位：{{ locationText }}</text>
          <text class="coords" v-if="form.latitude">{{ form.latitude }}, {{ form.longitude }}</text>
        </view>
        <button class="location-btn" @click="chooseLocation">选择位置</button>
      </view>
    </view>

    <view class="card">
      <text class="card-title">车位描述</text>
      <textarea class="textarea" v-model="form.description" placeholder="补充车位描述（可选）"></textarea>
    </view>

    <view class="card">
      <text class="card-title">车位图片</text>
      <view class="image-list" v-if="images.length > 0">
        <view class="image-item" v-for="(img, index) in images" :key="index">
          <image class="image" :src="img" mode="aspectFill" />
          <text class="remove" @click="removeImage(index)">×</text>
        </view>
      </view>
      <button class="add-image" @click="chooseImages">添加图片</button>
      <text class="hint">建议上传1-6张图片</text>
    </view>

    <view class="submit-bar">
      <button class="submit-btn" @click="handleSubmit" :loading="submitting">提交发布</button>
    </view>
  </view>
</template>

<script>
import { publishParking } from '../../api/parking.js'

export default {
  data() {
    return {
      form: {
        title: '',
        pricePerHour: '',
        spaceNumber: '',
        communityName: '',
        address: '',
        latitude: null,
        longitude: null,
        description: ''
      },
      images: [],
      locationText: '未选择',
      submitting: false
    }
  },
  methods: {
    chooseImages() {
      uni.chooseImage({
        count: 6,
        success: (res) => {
          this.images = [...this.images, ...res.tempFilePaths]
        }
      })
    },
    removeImage(index) {
      this.images.splice(index, 1)
    },
    chooseLocation() {
      if (uni.chooseLocation) {
        uni.chooseLocation({
          success: (res) => {
            this.form.communityName = res.name || this.form.communityName
            this.form.address = res.address || this.form.address
            this.form.latitude = res.latitude
            this.form.longitude = res.longitude
            this.locationText = res.name || res.address || '已选择'
          },
          fail: () => {
            uni.showToast({ title: '定位失败，请手动填写', icon: 'none' })
          }
        })
      } else {
        uni.getLocation({
          type: 'gcj02',
          success: (res) => {
            this.form.latitude = res.latitude
            this.form.longitude = res.longitude
            this.locationText = '已获取当前位置'
          },
          fail: () => {
            uni.showToast({ title: '定位失败，请手动填写', icon: 'none' })
          }
        })
      }
    },
    validateForm() {
      if (!this.form.title) return '请输入车位标题'
      if (!this.form.pricePerHour) return '请输入价格'
      if (!this.form.communityName) return '请输入小区/地点'
      if (!this.form.address) return '请输入详细地址'
      if (!this.form.latitude || !this.form.longitude) return '请填写定位信息'
      return ''
    },
    async handleSubmit() {
      const error = this.validateForm()
      if (error) {
        uni.showToast({ title: error, icon: 'none' })
        return
      }

      this.submitting = true
      try {
        const payload = {
          title: this.form.title,
          description: this.form.description,
          communityName: this.form.communityName,
          address: this.form.address,
          longitude: parseFloat(this.form.longitude),
          latitude: parseFloat(this.form.latitude),
          spaceNumber: this.form.spaceNumber,
          pricePerHour: parseFloat(this.form.pricePerHour),
          images: JSON.stringify(this.images || [])
        }
        const res = await publishParking(payload)
        if (res.code === 200) {
          uni.showToast({ title: '发布成功，等待审核', icon: 'success' })
          setTimeout(() => {
            uni.redirectTo({ url: '/pages/parking/myspaces' })
          }, 1200)
        } else {
          uni.showToast({ title: res.message || '发布失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '发布失败，请重试', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.card {
  background: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-row {
  display: flex;
  gap: 20rpx;
}

.form-item.half {
  flex: 1;
}

.label {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: block;
}

.input {
  width: 100%;
  height: 76rpx;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  background: #fafafa;
}

.textarea {
  width: 100%;
  min-height: 160rpx;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  background: #fafafa;
}

.location-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fa;
  padding: 20rpx;
  border-radius: 12rpx;
}

.location-info {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.location-text {
  font-size: 26rpx;
  color: #333;
}

.coords {
  font-size: 22rpx;
  color: #999;
}

.location-btn {
  background: #007bff;
  color: #fff;
  font-size: 24rpx;
  height: 64rpx;
  line-height: 64rpx;
  padding: 0 20rpx;
  border-radius: 32rpx;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.image-item {
  position: relative;
  width: 200rpx;
  height: 160rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.image {
  width: 100%;
  height: 100%;
}

.remove {
  position: absolute;
  top: 6rpx;
  right: 6rpx;
  width: 36rpx;
  height: 36rpx;
  line-height: 36rpx;
  text-align: center;
  background: rgba(0,0,0,0.5);
  color: #fff;
  border-radius: 50%;
  font-size: 24rpx;
}

.add-image {
  width: 100%;
  height: 72rpx;
  line-height: 72rpx;
  background: #f0f4ff;
  color: #007bff;
  border: 1rpx dashed #007bff;
  border-radius: 10rpx;
  font-size: 26rpx;
}

.hint {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #999;
}

.submit-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 30rpx;
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.08);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: #fff;
  font-size: 30rpx;
  font-weight: 500;
  border: none;
}
</style>
