/**
 * 信用分相关API
 */
import request from '../utils/request.js'

// 获取当前信用分
export const getCreditScore = () => {
  return request.get('/v1/credit/score')
}

// 获取信用记录
export const getCreditRecords = (pageNum = 1, pageSize = 20) => {
  return request.get('/v1/credit/records', { pageNum, pageSize })
}

// 检查信用是否满足要求
export const checkCreditScore = (requiredScore = 60) => {
  return request.get('/v1/credit/check', { requiredScore })
}

// 信用分变动类型常量
export const CreditChangeType = {
  COMPLETE_ORDER: 1,   // 完成订单
  TIMEOUT: 2,          // 超时未核销
  CANCEL: 3,           // 临时取消
  COMPLAINT: 4,        // 被投诉
  SYSTEM_ADJUST: 5     // 系统调整
}

// 变动类型文本
export const getChangeTypeText = (type) => {
  const typeMap = {
    1: '完成订单',
    2: '超时未核销',
    3: '临时取消',
    4: '被投诉',
    5: '系统调整'
  }
  return typeMap[type] || '未知'
}
