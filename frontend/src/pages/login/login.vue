<template>
  <view class="login-page">
    <view class="auth-checking" v-if="checkingAuth">
      <text class="checking-text">正在检查登录状态...</text>
    </view>
    <view v-else class="login-container">
      <view class="login-header">
        <text class="title">车位共享平台</text>
        <text class="subtitle">Parking Sharing Platform</text>
      </view>

      <view class="login-form">
        <view class="mode-switch">
          <view class="mode-item" :class="{ active: !isRegister }" @click="switchMode(false)">登录</view>
          <view class="mode-item" :class="{ active: isRegister }" @click="switchMode(true)">注册</view>
        </view>

        <view class="form-item" v-if="isRegister">
          <text class="label">昵称</text>
          <input class="input" type="text" v-model="nickname" placeholder="请输入昵称（可选）" />
        </view>

        <view class="form-item">
          <text class="label">用户名</text>
          <input class="input" type="text" v-model="username" placeholder="请输入用户名" />
        </view>

        <view class="form-item" v-if="isRegister">
          <text class="label">手机号</text>
          <input class="input" type="number" v-model="phone" placeholder="请输入手机号（可选）" />
        </view>

        <view class="form-item">
          <text class="label">密码</text>
          <input class="input" type="password" v-model="password" placeholder="请输入密码" :password="true" />
        </view>

        <view class="form-item" v-if="isRegister">
          <text class="label">确认密码</text>
          <input class="input" type="password" v-model="confirmPassword" placeholder="请再次输入密码" :password="true" />
        </view>

        <button class="login-btn" @click="handleSubmit" :loading="loading">
          {{ isRegister ? '注册并登录' : '登录' }}
        </button>

        <view class="error-msg" v-if="errorMessage">
          <text>{{ errorMessage }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { userLogin, register, adminLogin } from '../../api/user.js'
import { setToken, setUserInfo, getToken, getUserInfo } from '../../utils/auth.js'

export default {
  data() {
    return {
      checkingAuth: true,
      isRegister: false,
      username: '',
      password: '',
      confirmPassword: '',
      phone: '',
      nickname: '',
      errorMessage: '',
      loading: false
    }
  },
  onLoad() {
    if (getToken()) {
      this.navigateAfterLogin(getUserInfo() || {})
      return
    }
    this.checkingAuth = false
  },
  methods: {
    switchMode(registerMode) {
      this.isRegister = registerMode
      this.errorMessage = ''
    },
    async handleSubmit() {
      if (this.loading) return
      if (this.isRegister) {
        await this.handleRegister()
      } else {
        await this.handleLogin()
      }
    },
    async handleLogin() {
      const username = (this.username || '').trim()
      const password = this.password || ''
      if (!username || !password) {
        this.errorMessage = '请输入用户名和密码'
        return
      }

      this.loading = true
      this.errorMessage = ''
      try {
        const credentials = { username, password }

        const userRes = await userLogin(credentials)
        if (this.applyLoginResult(userRes, { username, role: '用户' }, true)) {
          return
        }

        const adminRes = await adminLogin(credentials)
        this.applyLoginResult(adminRes, { username, role: '管理员', isAdmin: true })
      } catch (error) {
        this.errorMessage = error?.message || '登录失败，请重试'
      } finally {
        this.loading = false
      }
    },
    async handleRegister() {
      if (!this.username || !this.password) {
        this.errorMessage = '请输入用户名和密码'
        return
      }
      if (this.password.length < 6) {
        this.errorMessage = '密码长度不能少于6位'
        return
      }
      if (this.password !== this.confirmPassword) {
        this.errorMessage = '两次输入密码不一致'
        return
      }

      this.loading = true
      this.errorMessage = ''
      try {
        const res = await register({
          username: this.username,
          password: this.password,
          phone: this.phone,
          nickname: this.nickname
        })
        this.applyLoginResult(res)
      } catch (error) {
        this.errorMessage = error?.message || '注册失败，请重试'
      } finally {
        this.loading = false
      }
    },
    applyLoginResult(res, fallbackUser = null, silent = false) {
      if (res.code !== 200 && !res.success) {
        if (!silent) {
          this.errorMessage = res.message || '操作失败'
        }
        return false
      }

      const data = res.data || {}
      const token = data.token || res.token
      const rawUser = data.user || data
      const looksLikeUser = rawUser && typeof rawUser === 'object' && !Array.isArray(rawUser) && (
        Object.prototype.hasOwnProperty.call(rawUser, 'id')
        || Object.prototype.hasOwnProperty.call(rawUser, 'username')
        || Object.prototype.hasOwnProperty.call(rawUser, 'nickname')
        || Object.prototype.hasOwnProperty.call(rawUser, 'role')
      )
      const user = looksLikeUser ? { ...(fallbackUser || {}), ...rawUser } : (fallbackUser || {})

      if (!token) {
        if (!silent) {
          this.errorMessage = '登录结果缺少令牌'
        }
        return false
      }

      setToken(token)
      setUserInfo(user)
      this.$store.commit('SET_USER', user)
      this.$store.commit('SET_TOKEN', token)

      this.navigateAfterLogin(user)
      return true
    },
    navigateAfterLogin(user) {
      const safeUser = user || {}
      /* #ifdef H5 */
      if (safeUser.isAdmin) {
        uni.reLaunch({ url: '/pages/dashboard/dashboard' })
        return
      }
      /* #endif */
      uni.switchTab({
        url: '/pages/index/index',
        fail: () => {
          uni.reLaunch({ url: '/pages/index/index' })
        }
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #35424a 0%, #007bff 100%);
  padding: 40rpx;
}

.login-container {
  width: 100%;
}

.auth-checking {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  width: 100%;
}

.checking-text {
  color: rgba(255, 255, 255, 0.86);
  font-size: 28rpx;
}

.login-header {
  text-align: center;
  margin-bottom: 80rpx;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  color: #fff;
  display: block;
}

.subtitle {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.72);
  margin-top: 10rpx;
  display: block;
}

.login-form {
  width: 100%;
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.15);
}

.mode-switch {
  display: flex;
  background: #f3f6fb;
  border-radius: 999rpx;
  padding: 6rpx;
  margin-bottom: 30rpx;
}

.mode-item {
  flex: 1;
  text-align: center;
  height: 60rpx;
  line-height: 60rpx;
  color: #5b6b82;
  font-size: 26rpx;
  border-radius: 999rpx;
}

.mode-item.active {
  background: #007bff;
  color: #fff;
}

.form-item {
  margin-bottom: 22rpx;
}

.label {
  font-size: 26rpx;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.input {
  width: 100%;
  height: 78rpx;
  border: 1rpx solid #ddd;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.login-btn {
  width: 100%;
  height: 88rpx;
  background-color: #007bff;
  color: #fff;
  font-size: 32rpx;
  border: none;
  border-radius: 10rpx;
  margin-top: 20rpx;
}

.error-msg {
  text-align: center;
  color: #dc3545;
  font-size: 26rpx;
  margin-top: 18rpx;
}
</style>
