package com.graduation.platform.controller;

import com.graduation.platform.model.dto.OrderDTO;
import com.graduation.platform.model.entity.Order;
import com.graduation.platform.service.OrderService;
import com.graduation.platform.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(orderDTO);
        return Result.success(order);
    }

    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return Result.success(order);
    }

    @GetMapping
    public Result<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Result.success(orders);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.success(null);
    }
}