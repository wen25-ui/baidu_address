/**
 * User related API.
 */
import request from '../utils/request.js'

// WeChat mini-program login
export const wxLogin = (data) => {
  return request.post('/v1/user/wx-login', data)
}

// User account login
export const userLogin = (data) => {
  return request.post('/v1/user/login', data)
}

// User account register
export const register = (data) => {
  return request.post('/v1/user/register', data)
}

// Admin login
export const adminLogin = (data) => {
  return request.post('/admin/login', data)
}

// Get current user info
export const getUserInfo = () => {
  return request.get('/v1/user/info')
}

// Update current user info
export const updateUserInfo = (data) => {
  return request.put('/v1/user/info', data)
}

// Real-name verify
export const verifyUser = (data) => {
  return request.post('/v1/user/verify', data)
}

// Credit score check
export const checkCredit = (requiredScore = 60) => {
  return request.get('/v1/user/credit-check', { requiredScore })
}

// Admin: list users
export const getAllUsers = (params = {}) => {
  const { keyword, status, pageNum = 1, pageSize = 10 } = params
  return request.get('/admin/users', { keyword, status, pageNum, pageSize })
}

// Admin: update user status
export const updateUserStatus = (userId, status) => {
  return request.put(`/admin/users/${userId}/status`, { status })
}

// Admin: disable user (soft delete)
export const deleteUser = (userId) => {
  return updateUserStatus(userId, 0)
}
