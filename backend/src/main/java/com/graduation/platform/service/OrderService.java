package com.graduation.platform.service;

import com.graduation.platform.model.dto.OrderDTO;
import com.graduation.platform.model.vo.OrderVO;

import java.util.List;

public interface OrderService {
    OrderVO createOrder(OrderDTO orderDTO);
    OrderVO getOrderById(Long orderId);
    List<OrderVO> getAllOrders();
    void updateOrder(Long orderId, OrderDTO orderDTO);
    void deleteOrder(Long orderId);
}