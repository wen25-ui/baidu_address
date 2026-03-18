/**
 * 钱包相关API
 */
import request from '../utils/request.js'

// 获取钱包信息
export const getWallet = () => {
  return request.get('/v1/wallet')
}

// 充值
export const recharge = (amount) => {
  const value = typeof amount === 'object' ? amount.amount : amount
  return request.post('/v1/wallet/recharge', { amount: value })
}

// 提现
export const withdraw = (amount) => {
  const value = typeof amount === 'object' ? amount.amount : amount
  return request.post('/v1/wallet/withdraw', { amount: value })
}

// 获取交易记录
export const getTransactions = (type, pageNum = 1, pageSize = 20) => {
  const params = { pageNum, pageSize }
  if (typeof type === 'object' && type !== null) {
    const { type: t, page, pageNum: pn, pageSize: ps } = type
    params.pageNum = pn || page || pageNum
    params.pageSize = ps || pageSize
    if (t !== undefined && t !== null) {
      params.type = t
    }
  } else if (type !== undefined && type !== null) {
    params.type = type
  }
  return request.get('/v1/wallet/transactions', params)
}

// 获取钱包统计
export const getWalletStats = () => {
  return request.get('/v1/wallet/stats')
}

// 交易类型常量
export const TransactionType = {
  EXPENSE: 1,     // 支出
  INCOME: 2,      // 收入
  WITHDRAW: 3,    // 提现
  REFUND: 4,      // 退款
  RECHARGE: 5     // 充值
}

// 交易类型文本
export const getTransactionTypeText = (type) => {
  const typeMap = {
    1: '支出',
    2: '收入',
    3: '提现',
    4: '退款',
    5: '充值'
  }
  return typeMap[type] || '未知'
}
