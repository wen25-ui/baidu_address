package com.graduation.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.model.entity.Reservation;

import java.time.LocalDateTime;

/**
 * 预约订单服务接口
 */
public interface ReservationService {

    /**
     * 创建预约订单
     * @param userId 用户ID
     * @param spaceId 车位ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 预约订单
     */
    Reservation create(Long userId, Long spaceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据ID获取订单详情
     * @param id 订单ID
     * @return 订单信息
     */
    Reservation getById(Long id);

    /**
     * 根据订单号获取订单详情
     * @param orderNo 订单号
     * @return 订单信息
     */
    Reservation getByOrderNo(String orderNo);

    /**
     * 支付订单
     * @param id 订单ID
     * @param userId 用户ID（验证权限）
     * @return 是否成功
     */
    boolean pay(Long id, Long userId);

    /**
     * 取消订单
     * @param id 订单ID
     * @param userId 用户ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancel(Long id, Long userId, String reason);

    /**
     * 核销订单（开始使用）
     * @param id 订单ID
     * @param verifyCode 核销码
     * @param ownerId 车位主人ID（验证权限）
     * @return 是否成功
     */
    boolean verify(Long id, String verifyCode, Long ownerId);

    /**
     * 完成订单
     * @param id 订单ID
     * @param ownerId 车位主人ID
     * @return 是否成功
     */
    boolean complete(Long id, Long ownerId);

    /**
     * 获取用户的订单列表
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param page 分页参数
     * @return 订单列表
     */
    Page<Reservation> getUserOrders(Long userId, Integer status, Page<Reservation> page);

    /**
     * 获取车位主人的订单列表
     * @param ownerId 车位主人ID
     * @param status 订单状态（可选）
     * @param page 分页参数
     * @return 订单列表
     */
    Page<Reservation> getOwnerOrders(Long ownerId, Integer status, Page<Reservation> page);

    /**
     * 获取车位的订单列表
     * @param spaceId 车位ID
     * @param status 订单状态（可选）
     * @param page 分页参数
     * @return 订单列表
     */
    Page<Reservation> getSpaceOrders(Long spaceId, Integer status, Page<Reservation> page);

    /**
     * 检查时段是否已被预约
     * @param spaceId 车位ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeReservationId 排除的订单ID（用于更新订单时）
     * @return 是否已被预约
     */
    boolean isTimeSlotBooked(Long spaceId, LocalDateTime startTime, LocalDateTime endTime, Long excludeReservationId);

    /**
     * 获取用户订单统计
     * @param userId 用户ID
     * @return 统计数据
     */
    ReservationStats getUserStats(Long userId);

    /**
     * 获取车位主人订单统计
     * @param ownerId 车位主人ID
     * @return 统计数据
     */
    ReservationStats getOwnerStats(Long ownerId);

    /**
     * 处理超时订单
     * 系统定时任务调用，将超时未支付的订单标记为已取消
     * @return 处理的订单数量
     */
    int handleTimeoutOrders();

    /**
     * 自动完成订单
     * 系统定时任务调用，将已核销且超过结束时间的订单标记为已完成
     * @return 处理的订单数量
     */
    int autoCompleteOrders();

    /**
     * 订单统计信息
     */
    record ReservationStats(
        int totalCount,
        int pendingPayCount,
        int pendingUseCount,
        int usingCount,
        int completedCount,
        int cancelledCount,
        java.math.BigDecimal totalAmount,
        java.math.BigDecimal completedAmount
    ) {}
}
