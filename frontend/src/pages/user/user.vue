<template>
  <view class="user-page">
    <view class="card" v-if="!isAdminView">
      <text class="card-title">我的账户</text>
      <view class="profile-row">
        <text class="profile-label">用户名</text>
        <text class="profile-value">{{ currentUser?.username || '-' }}</text>
      </view>
      <view class="profile-row">
        <text class="profile-label">身份</text>
        <text class="profile-value">{{ currentUser?.role || '用户' }}</text>
      </view>
      <view class="quick-nav">
        <button class="tiny-btn plain" @click="goTo('/pages/user/profile')">个人信息</button>
        <button class="tiny-btn plain" @click="goTo('/pages/user/verify')">实名认证</button>
        <button class="tiny-btn plain" @click="goTo('/pages/wallet/wallet')">我的钱包</button>
        <button class="tiny-btn plain" @click="goTo('/pages/credit/credit')">信用分</button>
      </view>
      <view class="actions">
        <button class="tiny-btn" @click="goHome">返回首页</button>
        <button class="tiny-btn bad-btn" @click="handleLogout">退出登录</button>
      </view>
    </view>

    <view v-else>
      <view class="card">
        <view class="admin-head">
          <text class="card-title">用户管理</text>
          <button class="tiny-btn bad-btn" @click="handleLogout">退出登录</button>
        </view>
        <view class="quick-nav">
          <button class="tiny-btn plain" @click="goTo('/pages/dashboard/dashboard')">控制台</button>
          <button class="tiny-btn plain" @click="goTo('/pages/navigation/navigation')">车位审核</button>
          <button class="tiny-btn plain" @click="goTo('/pages/order/order')">订单管理</button>
          <button class="tiny-btn plain" @click="goTo('/pages/billing/billing')">计费统计</button>
        </view>
      </view>

      <view class="card">
        <view class="filters">
          <input
            class="filter-input"
            v-model="filters.keyword"
            placeholder="用户名/昵称/手机号"
            @confirm="handleSearch"
          />
          <picker :range="statusLabels" :value="statusIndex" @change="onStatusChange">
            <view class="filter-picker">{{ statusLabels[statusIndex] }}</view>
          </picker>
          <button class="tiny-btn" :loading="loading" @click="handleSearch">查询</button>
        </view>
        <view class="summary-row">
          <text class="summary-text">共 {{ total }} 位用户</text>
          <button class="tiny-btn plain" :loading="loading" @click="fetchUsers">刷新</button>
        </view>

        <view v-if="users.length === 0 && !loading" class="empty-state">
          <text>暂无用户数据</text>
        </view>

        <view v-for="user in users" :key="user.id" class="user-item">
          <view class="main-row">
            <text class="user-name">{{ user.username || user.nickname || ('用户#' + user.id) }}</text>
            <text class="status-tag" :class="Number(user.status) === 1 ? 'ok' : 'bad'">
              {{ Number(user.status) === 1 ? '正常' : '禁用' }}
            </text>
          </view>
          <view class="info-row">
            <text>ID：{{ user.id }}</text>
            <text>昵称：{{ user.nickname || '-' }}</text>
            <text>手机号：{{ user.phone || '-' }}</text>
            <text>信用分：{{ user.creditScore || 0 }}</text>
          </view>
          <view class="actions">
            <button
              v-if="Number(user.status) !== 1"
              class="tiny-btn ok-btn"
              @click="changeUserStatus(user, 1)"
            >
              启用
            </button>
            <button
              v-if="Number(user.status) === 1"
              class="tiny-btn bad-btn"
              @click="changeUserStatus(user, 0)"
            >
              禁用
            </button>
          </view>
        </view>

        <view class="pager" v-if="total > filters.pageSize">
          <button class="tiny-btn plain" :disabled="filters.pageNum <= 1 || loading" @click="prevPage">上一页</button>
          <text class="pager-text">{{ filters.pageNum }} / {{ totalPages }}</text>
          <button class="tiny-btn plain" :disabled="filters.pageNum >= totalPages || loading" @click="nextPage">下一页</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getAdminUsers, updateAdminUserStatus } from '../../api/admin.js'
import { getToken, getUserInfo, removeToken, removeUserInfo } from '../../utils/auth.js'

const statusValues = ['', '1', '0']
const statusLabels = ['全部状态', '正常', '禁用']

export default {
  data() {
    return {
      currentUser: null,
      isAdminView: false,
      loading: false,
      users: [],
      total: 0,
      statusLabels,
      statusIndex: 0,
      filters: {
        keyword: '',
        status: '',
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  computed: {
    totalPages() {
      const pages = Math.ceil(this.total / this.filters.pageSize)
      return pages > 0 ? pages : 1
    }
  },
  onShow() {
    this.currentUser = getUserInfo() || {}
    this.isAdminView = !!this.currentUser?.isAdmin
    if (!getToken()) {
      uni.reLaunch({ url: '/pages/login/login' })
      return
    }
    if (this.isAdminView) {
      this.fetchUsers()
    }
  },
  methods: {
    goHome() {
      uni.switchTab({ url: '/pages/index/index' })
    },
    goTo(url) {
      uni.navigateTo({ url })
    },
    onStatusChange(event) {
      this.statusIndex = Number(event.detail.value || 0)
      this.filters.status = statusValues[this.statusIndex]
      this.filters.pageNum = 1
      this.fetchUsers()
    },
    handleSearch() {
      this.filters.pageNum = 1
      this.fetchUsers()
    },
    async fetchUsers() {
      this.loading = true
      try {
        const res = await getAdminUsers(this.filters)
        const page = res.data || {}
        this.users = page.records || []
        this.total = Number(page.total || 0)
      } catch (error) {
        uni.showToast({ title: error?.message || '获取用户失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    changeUserStatus(user, status) {
      const text = status === 1 ? '启用' : '禁用'
      uni.showModal({
        title: `确认${text}`,
        content: `是否${text}用户【${user.username || user.id}】？`,
        success: async (res) => {
          if (!res.confirm) return
          try {
            await updateAdminUserStatus(user.id, status)
            uni.showToast({ title: `${text}成功`, icon: 'success' })
            this.fetchUsers()
          } catch (error) {
            uni.showToast({ title: error?.message || `${text}失败`, icon: 'none' })
          }
        }
      })
    },
    prevPage() {
      if (this.filters.pageNum <= 1) return
      this.filters.pageNum -= 1
      this.fetchUsers()
    },
    nextPage() {
      if (this.filters.pageNum >= this.totalPages) return
      this.filters.pageNum += 1
      this.fetchUsers()
    },
    handleLogout() {
      removeToken()
      removeUserInfo()
      this.$store.commit('LOGOUT')
      uni.reLaunch({ url: '/pages/login/login' })
    }
  }
}
</script>

<style scoped>
.user-page {
  padding: 20rpx;
}

.card {
  background: #fff;
  border-radius: 14rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.08);
}

.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #1f2937;
}

.profile-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #edf0f5;
  min-height: 74rpx;
}

.profile-row:last-child {
  border-bottom: none;
}

.profile-label {
  color: #667085;
  font-size: 24rpx;
}

.profile-value {
  color: #1f2937;
  font-size: 26rpx;
  font-weight: 600;
}

.admin-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.quick-nav {
  margin-top: 14rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.filters {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.filter-input {
  flex: 1;
  height: 64rpx;
  border-radius: 10rpx;
  background: #f5f7fb;
  padding: 0 16rpx;
  font-size: 24rpx;
}

.filter-picker {
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 10rpx;
  background: #f5f7fb;
  padding: 0 16rpx;
  font-size: 24rpx;
  color: #344054;
}

.summary-row {
  margin-top: 12rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-text {
  font-size: 24rpx;
  color: #667085;
}

.user-item {
  margin-top: 12rpx;
  border: 1rpx solid #edf0f5;
  border-radius: 10rpx;
  padding: 14rpx;
}

.main-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #1f2937;
}

.status-tag {
  font-size: 22rpx;
  border-radius: 20rpx;
  padding: 4rpx 12rpx;
}

.status-tag.ok {
  color: #0f9d58;
  background: #e8f8ef;
}

.status-tag.bad {
  color: #dc2626;
  background: #ffebee;
}

.info-row {
  margin-top: 10rpx;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  font-size: 22rpx;
  color: #475467;
}

.actions {
  margin-top: 12rpx;
  display: flex;
  gap: 10rpx;
}

.tiny-btn {
  height: 52rpx;
  line-height: 52rpx;
  border-radius: 26rpx;
  background: #1f6fff;
  color: #fff;
  font-size: 22rpx;
  border: none;
  padding: 0 18rpx;
}

.tiny-btn.plain {
  background: #eef5ff;
  color: #1f6fff;
}

.tiny-btn.ok-btn {
  background: #e8f8ef;
  color: #0f9d58;
}

.tiny-btn.bad-btn {
  background: #ffebee;
  color: #dc2626;
}

.pager {
  margin-top: 16rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16rpx;
}

.pager-text {
  font-size: 24rpx;
  color: #475467;
}
</style>
