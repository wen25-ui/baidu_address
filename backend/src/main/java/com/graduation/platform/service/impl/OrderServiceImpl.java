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
        order.setStartLocation(orderDTO.getStartLocation());
        order.setEndLocation(orderDTO.getEndLocation());
        order.setDuration(orderDTO.getDuration());
        order.setPrice(calculatePrice(orderDTO.getDuration()));
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

    private double calculatePrice(long duration) {
        double rate = 6.0; // Minimum rate per hour
        double maxRate = 10.0; // Maximum rate per hour
        double price = (duration / 3600.0) * rate; // Convert seconds to hours
        return Math.min(price, maxRate);
    }
}