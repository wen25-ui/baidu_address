package com.graduation.platform.service;

import com.graduation.platform.model.dto.OrderDTO;
import com.graduation.platform.model.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO);
    Order getOrderById(Long orderId);
    List<Order> getAllOrders();
    void deleteOrder(Long orderId);
}