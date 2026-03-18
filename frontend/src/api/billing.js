import {
  getIncomeStats,
  getAdminReservations
} from './admin.js'

export const getBillingRecords = async (params = {}) => {
  const res = await getAdminReservations({
    status: 3,
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 20
  })
  return res
}

export const getBillingSummary = (startDate, endDate) => getIncomeStats(startDate, endDate)

export const createBillingRecord = () =>
  Promise.reject(new Error('计费记录由订单结算自动生成，不支持手动创建'))

export const updateBillingRecord = () =>
  Promise.reject(new Error('计费记录不支持手动修改'))

export const deleteBillingRecord = () =>
  Promise.reject(new Error('计费记录不支持手动删除'))
