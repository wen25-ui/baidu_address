/**
 * 车位相关API
 */
import request from '../utils/request.js'

// 发布车位
export const publishParking = (data) => {
  return request.post('/v1/parking', data)
}

// 获取车位详情
export const getParkingDetail = (id) => {
  return request.get(`/v1/parking/${id}`)
}

// 更新车位
export const updateParking = (id, data) => {
  return request.put(`/v1/parking/${id}`, data)
}

// 删除车位
export const deleteParking = (id) => {
  return request.delete(`/v1/parking/${id}`)
}

// 上架车位
export const onlineParking = (id) => {
  return request.post(`/v1/parking/${id}/online`)
}

// 下架车位
export const offlineParking = (id) => {
  return request.post(`/v1/parking/${id}/offline`)
}

// 搜索附近车位
export const searchNearby = (latitude, longitude, radius = 3, pageNum = 1, pageSize = 10) => {
  return request.get('/v1/parking/nearby', { latitude, longitude, radius, pageNum, pageSize })
}

// 关键词搜索车位
export const searchParking = (keyword, pageNum = 1, pageSize = 10) => {
  return request.get('/v1/parking/search', { keyword, pageNum, pageSize })
}

// 获取我的车位列表
export const getMyParkingSpaces = (pageNum = 1, pageSize = 10) => {
  return request.get('/v1/parking/mine', { pageNum, pageSize })
}

// 获取车位统计
export const getParkingStats = () => {
  return request.get('/v1/parking/stats')
}

// 添加车位时段规则
export const addParkingRule = (spaceId, data) => {
  return request.post(`/v1/parking/${spaceId}/rules`, data)
}

// 获取车位时段规则
export const getParkingRules = (spaceId) => {
  return request.get(`/v1/parking/${spaceId}/rules`)
}

// 查询可用时段
export const getAvailableSlots = (spaceId, date) => {
  return request.get(`/v1/parking/${spaceId}/available`, { date })
}

// 删除时段规则
export const deleteParkingRule = (ruleId) => {
  return request.delete(`/v1/parking/rules/${ruleId}`)
}
