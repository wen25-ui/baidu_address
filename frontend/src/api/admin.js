import request from '../utils/request.js'

export const getAdminInfo = () => request.get('/admin/info')

export const changeAdminPassword = (oldPassword, newPassword) =>
  request.post('/admin/change-password', { oldPassword, newPassword })

export const getAdminUsers = (params = {}) => {
  const { keyword = '', status = '', pageNum = 1, pageSize = 10 } = params
  return request.get('/admin/users', { keyword, status, pageNum, pageSize })
}

export const updateAdminUserStatus = (userId, status) =>
  request.put(`/admin/users/${userId}/status`, { status })

export const getAdminParkingSpaces = (params = {}) => {
  const { keyword = '', status = '', pageNum = 1, pageSize = 10 } = params
  return request.get('/admin/parking-spaces', { keyword, status, pageNum, pageSize })
}

export const auditAdminParkingSpace = (spaceId, approved, rejectReason = '') =>
  request.post(`/admin/parking-spaces/${spaceId}/audit`, { approved, rejectReason })

export const forceOfflineAdminParkingSpace = (spaceId, reason = '管理员下架') =>
  request.post(`/admin/parking-spaces/${spaceId}/force-offline`, { reason })

export const getAdminReservations = (params = {}) => {
  const { orderNo = '', status = '', pageNum = 1, pageSize = 10 } = params
  return request.get('/admin/reservations', { orderNo, status, pageNum, pageSize })
}

export const getAdminReservationDetail = (id) =>
  request.get(`/admin/reservations/${id}`)

export const getPlatformStats = () =>
  request.get('/admin/stats/platform')

export const getIncomeStats = (startDate, endDate) => {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return request.get('/admin/stats/income', params)
}
