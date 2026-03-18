/**
 * 预约订单相关API
 */
import request from '../utils/request.js'

// 创建预约
export const createReservation = (data) => {
  return request.post('/v1/reservation', data)
}

// 获取订单详情
export const getReservationDetail = (id) => {
  return request.get(`/v1/reservation/${id}`)
}

// 根据订单号获取详情
export const getReservationByOrderNo = (orderNo) => {
  return request.get(`/v1/reservation/no/${orderNo}`)
}

// 支付订单
export const payReservation = (id) => {
  return request.post(`/v1/reservation/${id}/pay`)
}

// 取消订单
export const cancelReservation = (id, reason) => {
  return request.post(`/v1/reservation/${id}/cancel`, { reason })
}

// 核销订单（车位主人操作）
export const verifyReservation = (id, verifyCode) => {
  return request.post(`/v1/reservation/${id}/verify`, { verifyCode })
}

// 完成订单（车位主人操作）
export const completeReservation = (id) => {
  return request.post(`/v1/reservation/${id}/complete`)
}

// 获取我的订单列表（作为预约用户）
export const getMyReservations = (status, pageNum = 1, pageSize = 10) => {
  const params = { pageNum, pageSize }
  if (status !== undefined && status !== null) {
    params.status = status
  }
  return request.get('/v1/reservation/my', params)
}

// 兼容旧调用：获取我的订单列表
export const getUserReservations = (paramsOrStatus, pageNum = 1, pageSize = 10) => {
  if (typeof paramsOrStatus === 'object' && paramsOrStatus !== null) {
    const { status, page, pageNum: pn, pageSize: ps } = paramsOrStatus
    return getMyReservations(status, pn || page || pageNum, ps || pageSize)
  }
  return getMyReservations(paramsOrStatus, pageNum, pageSize)
}

// 获取我收到的订单列表（作为车位主人）
export const getReceivedReservations = (status, pageNum = 1, pageSize = 10) => {
  const params = { pageNum, pageSize }
  if (status !== undefined && status !== null) {
    params.status = status
  }
  return request.get('/v1/reservation/received', params)
}

// 获取车位订单列表
export const getSpaceReservations = (spaceId, status, pageNum = 1, pageSize = 10) => {
  const params = { pageNum, pageSize }
  if (status !== undefined && status !== null) {
    params.status = status
  }
  return request.get(`/v1/reservation/space/${spaceId}`, params)
}

// 检查时段是否可预约
export const checkAvailable = (spaceId, startTime, endTime) => {
  return request.get('/v1/reservation/check-available', { spaceId, startTime, endTime })
}

// 获取用户订单统计
export const getUserReservationStats = () => {
  return request.get('/v1/reservation/stats/user')
}

// 获取车位主人订单统计
export const getOwnerReservationStats = () => {
  return request.get('/v1/reservation/stats/owner')
}

// 订单状态常量
export const ReservationStatus = {
  PENDING_PAY: 0,    // 待支付
  PENDING_USE: 1,    // 待使用
  USING: 2,          // 使用中
  COMPLETED: 3,      // 已完成
  CANCELLED: 4,      // 已取消
  TIMEOUT: 5         // 已超时
}

// 订单状态文本
export const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '待使用',
    2: '使用中',
    3: '已完成',
    4: '已取消',
    5: '已超时'
  }
  return statusMap[status] || '未知'
}

// 订单状态颜色
export const getStatusColor = (status) => {
  const colorMap = {
    0: '#ff9800',
    1: '#2196f3',
    2: '#4caf50',
    3: '#9e9e9e',
    4: '#f44336',
    5: '#f44336'
  }
  return colorMap[status] || '#999999'
}
