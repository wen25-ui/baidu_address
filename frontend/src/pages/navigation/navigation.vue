<template>
  <view class="parking-admin-page">
    <view class="card">
      <text class="card-title">车位审核与管理</text>
      <view class="filters">
        <input
          class="filter-input"
          v-model="filters.keyword"
          placeholder="车位标题/小区/地址关键词"
          @confirm="handleSearch"
        />
        <picker :range="statusLabels" :value="statusIndex" @change="onStatusChange">
          <view class="filter-picker">{{ statusLabels[statusIndex] }}</view>
        </picker>
        <button class="search-btn" :loading="loading" @click="handleSearch">查询</button>
      </view>
    </view>

    <view class="card">
      <view class="summary-row">
        <text class="summary-text">共 {{ total }} 条车位</text>
        <button class="tiny-btn" :loading="loading" @click="fetchSpaces">刷新</button>
      </view>

      <view v-if="spaces.length === 0 && !loading" class="empty-state">
        <text>暂无车位数据</text>
      </view>

      <view v-for="item in spaces" :key="item.id" class="space-item">
        <view class="space-main">
          <text class="space-title">{{ item.title || ('车位#' + item.id) }}</text>
          <text class="status-tag" :class="statusClass(item.status)">{{ statusText(item.status) }}</text>
        </view>
        <view class="space-info">
          <text>车主ID：{{ item.ownerId }}</text>
          <text>地址：{{ item.communityName || '-' }} {{ item.address || '-' }}</text>
          <text>价格：¥{{ money(item.pricePerHour) }}/小时</text>
          <text v-if="item.rejectReason">驳回原因：{{ item.rejectReason }}</text>
        </view>
        <view class="actions">
          <button
            v-if="Number(item.status) === 0"
            class="tiny-btn ok-btn"
            @click="audit(item, true)"
          >
            审核通过
          </button>
          <button
            v-if="Number(item.status) === 0"
            class="tiny-btn bad-btn"
            @click="audit(item, false)"
          >
            驳回
          </button>
          <button
            v-if="Number(item.status) === 1"
            class="tiny-btn bad-btn"
            @click="forceOffline(item)"
          >
            强制下架
          </button>
        </view>
      </view>

      <view class="pager" v-if="total > filters.pageSize">
        <button class="tiny-btn" :disabled="filters.pageNum <= 1 || loading" @click="prevPage">上一页</button>
        <text class="pager-text">{{ filters.pageNum }} / {{ totalPages }}</text>
        <button class="tiny-btn" :disabled="filters.pageNum >= totalPages || loading" @click="nextPage">下一页</button>
      </view>
    </view>
  </view>
</template>

<script>
import {
  getAdminParkingSpaces,
  auditAdminParkingSpace,
  forceOfflineAdminParkingSpace
} from '../../api/admin.js'
import { getToken, getUserInfo } from '../../utils/auth.js'

const statusValues = ['', '0', '1', '2', '3']
const statusLabels = ['全部状态', '待审核', '已上架', '已下架', '审核拒绝']

export default {
  data() {
    return {
      loading: false,
      spaces: [],
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
    if (!this.ensureAdmin()) return
    this.fetchSpaces()
  },
  methods: {
    ensureAdmin() {
      const token = getToken()
      const user = getUserInfo()
      if (!token) {
        uni.reLaunch({ url: '/pages/login/login' })
        return false
      }
      /* #ifdef H5 */
      if (!user || !user.isAdmin) {
        uni.showToast({ title: '请使用管理员账号登录', icon: 'none' })
        uni.reLaunch({ url: '/pages/login/login' })
        return false
      }
      /* #endif */
      return true
    },
    onStatusChange(event) {
      this.statusIndex = Number(event.detail.value || 0)
      this.filters.status = statusValues[this.statusIndex]
      this.filters.pageNum = 1
      this.fetchSpaces()
    },
    handleSearch() {
      this.filters.pageNum = 1
      this.fetchSpaces()
    },
    async fetchSpaces() {
      this.loading = true
      try {
        const res = await getAdminParkingSpaces(this.filters)
        const page = res.data || {}
        this.spaces = page.records || []
        this.total = Number(page.total || 0)
      } catch (error) {
        uni.showToast({ title: error?.message || '获取车位失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    audit(item, approved) {
      const actionText = approved ? '通过' : '驳回'
      uni.showModal({
        title: `确认${actionText}`,
        content: `是否${actionText}车位【${item.title || item.id}】？`,
        success: async (modalRes) => {
          if (!modalRes.confirm) return
          try {
            const reason = approved ? '' : '管理员审核不通过'
            await auditAdminParkingSpace(item.id, approved, reason)
            uni.showToast({ title: `已${actionText}`, icon: 'success' })
            this.fetchSpaces()
          } catch (error) {
            uni.showToast({ title: error?.message || `${actionText}失败`, icon: 'none' })
          }
        }
      })
    },
    forceOffline(item) {
      uni.showModal({
        title: '确认下架',
        content: `是否强制下架车位【${item.title || item.id}】？`,
        success: async (modalRes) => {
          if (!modalRes.confirm) return
          try {
            await forceOfflineAdminParkingSpace(item.id, '管理员手动下架')
            uni.showToast({ title: '已下架', icon: 'success' })
            this.fetchSpaces()
          } catch (error) {
            uni.showToast({ title: error?.message || '下架失败', icon: 'none' })
          }
        }
      })
    },
    prevPage() {
      if (this.filters.pageNum <= 1) return
      this.filters.pageNum -= 1
      this.fetchSpaces()
    },
    nextPage() {
      if (this.filters.pageNum >= this.totalPages) return
      this.filters.pageNum += 1
      this.fetchSpaces()
    },
    statusText(status) {
      const map = { 0: '待审核', 1: '已上架', 2: '已下架', 3: '审核拒绝' }
      return map[Number(status)] || '未知'
    },
    statusClass(status) {
      const key = Number(status)
      if (key === 1) return 'ok'
      if (key === 0) return 'warn'
      if (key === 2 || key === 3) return 'bad'
      return ''
    },
    money(value) {
      const num = Number(value || 0)
      return Number.isFinite(num) ? num.toFixed(2) : '0.00'
    }
  }
}
</script>

<style scoped>
.parking-admin-page {
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
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  margin-bottom: 16rpx;
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

.search-btn {
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 10rpx;
  background: #1f6fff;
  color: #fff;
  font-size: 24rpx;
  padding: 0 20rpx;
  border: none;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.summary-text {
  font-size: 24rpx;
  color: #667085;
}

.space-item {
  border: 1rpx solid #edf0f5;
  border-radius: 10rpx;
  padding: 16rpx;
  margin-bottom: 12rpx;
}

.space-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.space-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #1f2937;
}

.status-tag {
  font-size: 22rpx;
  color: #64748b;
  background: #f1f5f9;
  border-radius: 20rpx;
  padding: 4rpx 12rpx;
}

.status-tag.ok {
  color: #0f9d58;
  background: #e8f8ef;
}

.status-tag.warn {
  color: #b7791f;
  background: #fff8e6;
}

.status-tag.bad {
  color: #dc2626;
  background: #ffebee;
}

.space-info {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  margin-top: 12rpx;
  color: #475467;
  font-size: 22rpx;
}

.actions {
  display: flex;
  gap: 8rpx;
  margin-top: 12rpx;
}

.tiny-btn {
  height: 52rpx;
  line-height: 52rpx;
  border-radius: 26rpx;
  background: #eef5ff;
  color: #1f6fff;
  font-size: 22rpx;
  border: none;
  padding: 0 18rpx;
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
  margin-top: 10rpx;
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
