package com.graduation.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.model.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌
     */
    String login(String username, String password);

    /**
     * 获取管理员信息
     * @param adminId 管理员ID
     * @return 管理员信息
     */
    Admin getById(Long adminId);

    /**
     * 修改密码
     * @param adminId 管理员ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long adminId, String oldPassword, String newPassword);

    // ========== 用户管理 ==========

    /**
     * 获取用户列表
     * @param keyword 搜索关键词
     * @param status 状态
     * @param page 分页
     * @return 用户列表
     */
    Page<User> getUserList(String keyword, Integer status, Page<User> page);

    /**
     * 禁用/启用用户
     * @param userId 用户ID
     * @param status 状态 0-禁用 1-启用
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, Integer status);

    // ========== 车位管理 ==========

    /**
     * 获取车位列表
     * @param keyword 搜索关键词
     * @param status 状态
     * @param page 分页
     * @return 车位列表
     */
    Page<ParkingSpace> getParkingSpaceList(String keyword, Integer status, Page<ParkingSpace> page);

    /**
     * 审核车位
     * @param spaceId 车位ID
     * @param approved 是否通过
     * @param rejectReason 拒绝原因（不通过时）
     * @return 是否成功
     */
    boolean auditParkingSpace(Long spaceId, boolean approved, String rejectReason);

    /**
     * 强制下架车位
     * @param spaceId 车位ID
     * @param reason 下架原因
     * @return 是否成功
     */
    boolean forceParkingSpaceOffline(Long spaceId, String reason);

    // ========== 订单管理 ==========

    /**
     * 获取订单列表
     * @param orderNo 订单号
     * @param status 状态
     * @param page 分页
     * @return 订单列表
     */
    Page<Reservation> getReservationList(String orderNo, Integer status, Page<Reservation> page);

    /**
     * 获取订单详情
     * @param reservationId 订单ID
     * @return 订单详情
     */
    Reservation getReservationDetail(Long reservationId);

    // ========== 数据统计 ==========

    /**
     * 获取平台统计数据
     * @return 统计数据
     */
    PlatformStats getPlatformStats();

    /**
     * 获取收入统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收入统计
     */
    IncomeStats getIncomeStats(LocalDate startDate, LocalDate endDate);

    /**
     * 平台统计数据
     */
    record PlatformStats(
        long totalUsers,
        long activeUsers,
        long totalParkingSpaces,
        long onlineParkingSpaces,
        long totalReservations,
        long completedReservations,
        BigDecimal totalRevenue,
        BigDecimal platformRevenue
    ) {}

    /**
     * 收入统计
     */
    record IncomeStats(
        BigDecimal totalAmount,
        BigDecimal platformFee,
        int orderCount,
        BigDecimal avgOrderAmount
    ) {}
}
