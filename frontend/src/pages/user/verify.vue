<template>
  <view class="verify-page">
    <view class="card">
      <text class="card-title">实名认证</text>
      <view class="status-row">
        <text class="status-label">当前状态：</text>
        <text class="status-value" :class="user.isVerified ? 'verified' : 'unverified'">
          {{ user.isVerified ? '已认证' : '未认证' }}
        </text>
      </view>
      <view class="form-item">
        <text class="label">真实姓名</text>
        <input class="input" v-model="realName" placeholder="请输入真实姓名" :disabled="user.isVerified" />
      </view>
      <view class="form-item">
        <text class="label">身份证号</text>
        <input class="input" v-model="idCard" placeholder="请输入身份证号" :disabled="user.isVerified" />
      </view>
      <button class="submit-btn" @click="submitVerify" :loading="submitting" :disabled="user.isVerified">提交认证</button>
    </view>
  </view>
</template>

<script>
import { getUserInfo, verifyUser } from '../../api/user.js'

export default {
  data() {
    return {
      user: {},
      realName: '',
      idCard: '',
      submitting: false
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
          this.realName = this.user.realName || ''
          this.idCard = this.user.idCard || ''
        }
      } catch (e) {
        console.error('加载用户信息失败:', e)
      }
    },
    async submitVerify() {
      if (this.user.isVerified) return
      if (!this.realName || !this.idCard) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }
      this.submitting = true
      try {
        const res = await verifyUser({ realName: this.realName, idCard: this.idCard })
        if (res.code === 200) {
          uni.showToast({ title: '认证成功', icon: 'success' })
          this.loadUser()
        } else {
          uni.showToast({ title: res.message || '认证失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '认证失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.verify-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.card {
  background: #fff;
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

.status-row {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.status-label {
  font-size: 26rpx;
  color: #666;
}

.status-value {
  font-size: 26rpx;
  font-weight: 500;
}

.status-value.verified { color: #4caf50; }
.status-value.unverified { color: #ff9800; }

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

.submit-btn {
  width: 100%;
  height: 80rpx;
  border-radius: 40rpx;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: #fff;
  font-size: 28rpx;
  border: none;
}

.submit-btn[disabled] {
  background: #ccc;
}
</style>
