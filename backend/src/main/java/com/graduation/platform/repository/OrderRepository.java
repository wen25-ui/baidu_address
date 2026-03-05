package com.graduation.platform.repository;

import com.graduation.platform.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 这里可以添加自定义查询方法
}