package com.graduation.platform.controller;

import com.graduation.platform.model.dto.OrderDTO;
import com.graduation.platform.model.vo.OrderVO;
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
    public Result<OrderVO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderVO orderVO = orderService.createOrder(orderDTO);
        return Result.success(orderVO);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getOrderById(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderById(id);
        return Result.success(orderVO);
    }

    @GetMapping
    public Result<List<OrderVO>> getAllOrders() {
        List<OrderVO> orders = orderService.getAllOrders();
        return Result.success(orders);
    }

    @PutMapping("/{id}")
    public Result<OrderVO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        OrderVO orderVO = orderService.updateOrder(id, orderDTO);
        return Result.success(orderVO);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.success(null);
    }
}