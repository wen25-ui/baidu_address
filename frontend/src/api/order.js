import {
  getAdminReservations,
  getAdminReservationDetail
} from './admin.js'

export const getOrderList = (params = {}) => getAdminReservations(params)

export const getOrderDetail = (orderId) => getAdminReservationDetail(orderId)

// Admin side does not provide direct delete endpoint.
export const deleteOrder = (orderId) =>
  Promise.reject(new Error(`订单 ${orderId} 不支持直接删除，请在后端流程中取消`))

export const updateOrderStatus = () =>
  Promise.reject(new Error('管理端暂不支持手动更新订单状态'))
