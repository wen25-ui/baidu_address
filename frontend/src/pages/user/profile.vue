<template>
  <view class="profile-page">
    <view class="card profile-card">
      <view class="avatar-section" @click="chooseAvatar">
        <image v-if="user.avatar" class="avatar" :src="user.avatar" mode="aspectFill" />
        <view v-else class="avatar placeholder">
          <text class="avatar-text">{{ avatarText }}</text>
        </view>
        <text class="change-avatar">点击更换头像</text>
      </view>
    </view>

    <view class="card">
      <text class="card-title">基本信息</text>
      <view class="form-item">
        <text class="label">昵称</text>
        <input class="input" v-model="user.nickname" placeholder="请输入昵称" />
      </view>
      <view class="form-item">
        <text class="label">手机号</text>
        <input class="input" v-model="user.phone" placeholder="请输入手机号" />
      </view>
      <view class="form-item">
        <text class="label">实名认证</text>
        <text class="text-value">{{ user.isVerified ? '已认证' : '未认证' }}</text>
      </view>
      <view class="form-item" v-if="user.realName">
        <text class="label">真实姓名</text>
        <text class="text-value">{{ user.realName }}</text>
      </view>
    </view>

    <view class="submit-bar">
      <button class="submit-btn" @click="saveProfile" :loading="saving">保存修改</button>
    </view>
  </view>
</template>

<script>
import { getUserInfo, updateUserInfo } from '../../api/user.js'
import { setUserInfo } from '../../utils/auth.js'

export default {
  data() {
    return {
      user: {
        nickname: '',
        phone: '',
        avatar: ''
      },
      saving: false
    }
  },
  computed: {
    avatarText() {
      const name = this.user.nickname || this.user.realName || 'U'
      return name.charAt(0).toUpperCase()
    }
  },
  onShow() {
    this.loadUser()
  },
  methods: {
    async loadUser() {
      try {
        const res = await getUserInfo()
        if (res.code === 200) {
          this.user = res.data || {}
        }
      } catch (e) {
        console.error('加载用户信息失败:', e)
      }
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          this.user.avatar = res.tempFilePaths[0]
        }
      })
    },
    async saveProfile() {
      this.saving = true
      try {
        const payload = {
          nickname: this.user.nickname,
          phone: this.user.phone,
          avatar: this.user.avatar
        }
        const res = await updateUserInfo(payload)
        if (res.code === 200) {
          uni.showToast({ title: '保存成功', icon: 'success' })
          setUserInfo({ ...this.user, ...payload })
        } else {
          uni.showToast({ title: res.message || '保存失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '保存失败', icon: 'none' })
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
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

.profile-card {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-section {
  text-align: center;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background: #f0f0f0;
}

.avatar.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
}

.avatar-text {
  color: #fff;
  font-size: 56rpx;
  font-weight: bold;
}

.change-avatar {
  font-size: 24rpx;
  color: #007bff;
  margin-top: 12rpx;
  display: block;
}

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.form-item {
  margin-bottom: 20rpx;
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

.text-value {
  font-size: 28rpx;
  color: #333;
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
