import request from '../utils/request';

const orderApi = {
  // 创建订单
  createOrder(data) {
    return request.post('/api/orders', data);
  },

  // 获取订单列表
  getOrderList(params) {
    return request.get('/api/orders', { params });
  },

  // 获取订单详情
  getOrderDetail(orderId) {
    return request.get(`/api/orders/${orderId}`);
  },

  // 更新订单状态
  updateOrderStatus(orderId, data) {
    return request.put(`/api/orders/${orderId}/status`, data);
  },

  // 删除订单
  deleteOrder(orderId) {
    return request.delete(`/api/orders/${orderId}`);
  }
};

export default orderApi;