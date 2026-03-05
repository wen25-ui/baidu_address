package com.graduation.platform.service.impl;

import com.graduation.platform.model.dto.OrderDTO;
import com.graduation.platform.model.entity.Order;
import com.graduation.platform.repository.OrderRepository;
import com.graduation.platform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setStartLocation(orderDTO.getRoute());
        order.setEndLocation(orderDTO.getRoute());
        order.setStartTime(orderDTO.getStartTime());
        order.setEndTime(orderDTO.getEndTime());
        order.setTotalCost(orderDTO.getTotalCost() != null ? orderDTO.getTotalCost().doubleValue() : 0.0);
        order.setStatus(orderDTO.getStatus() != null ? orderDTO.getStatus() : "CREATED");
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}