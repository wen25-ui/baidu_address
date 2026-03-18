/**
 * 收藏相关API
 */
import request from '../utils/request.js'

// 添加收藏
export const addFavorite = (spaceId) => {
  return request.post(`/v1/favorite/${spaceId}`)
}

// 取消收藏
export const removeFavorite = (spaceId) => {
  return request.delete(`/v1/favorite/${spaceId}`)
}

// 检查是否已收藏
export const checkFavorite = (spaceId) => {
  return request.get(`/v1/favorite/${spaceId}/check`)
}

// 获取收藏列表
export const getFavorites = (pageNum = 1, pageSize = 10) => {
  return request.get('/v1/favorite/list', { pageNum, pageSize })
}

// 获取收藏数量
export const getFavoriteCount = () => {
  return request.get('/v1/favorite/count')
}
