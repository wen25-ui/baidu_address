package com.graduation.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.Result;
import com.graduation.platform.model.entity.Reservation;
import com.graduation.platform.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 预约订单控制器
 */
@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 创建预约订单
     */
    @PostMapping
    public Result<Reservation> create(@RequestBody CreateReservationRequest request, 
                                       HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Reservation reservation = reservationService.create(
                userId, 
                request.spaceId(), 
                request.startTime(), 
                request.endTime()
        );
        return Result.success(reservation);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Reservation> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Reservation reservation = reservationService.getById(id);
        if (reservation == null) {
            return Result.error("订单不存在");
        }
        // 只有用户本人或车位主人可以查看
        if (!reservation.getUserId().equals(userId) && !reservation.getOwnerId().equals(userId)) {
            return Result.error(403, "无权查看此订单");
        }
        return Result.success(reservation);
    }

    /**
     * 根据订单号获取订单详情
     */
    @GetMapping("/no/{orderNo}")
    public Result<Reservation> getByOrderNo(@PathVariable String orderNo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Reservation reservation = reservationService.getByOrderNo(orderNo);
        if (reservation == null) {
            return Result.error("订单不存在");
        }
        // 只有用户本人或车位主人可以查看
        if (!reservation.getUserId().equals(userId) && !reservation.getOwnerId().equals(userId)) {
            return Result.error(403, "无权查看此订单");
        }
        return Result.success(reservation);
    }

    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    public Result<Boolean> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(reservationService.pay(id, userId));
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id, 
                                   @RequestBody(required = false) CancelRequest cancelRequest,
                                   HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String reason = cancelRequest != null ? cancelRequest.reason() : null;
        return Result.success(reservationService.cancel(id, userId, reason));
    }

    /**
     * 核销订单（车位主人操作）
     */
    @PostMapping("/{id}/verify")
    public Result<Boolean> verify(@PathVariable Long id, 
                                   @RequestBody VerifyRequest verifyRequest,
                                   HttpServletRequest request) {
        Long ownerId = (Long) request.getAttribute("userId");
        return Result.success(reservationService.verify(id, verifyRequest.verifyCode(), ownerId));
    }

    /**
     * 完成订单（车位主人操作）
     */
    @PostMapping("/{id}/complete")
    public Result<Boolean> complete(@PathVariable Long id, HttpServletRequest request) {
        Long ownerId = (Long) request.getAttribute("userId");
        return Result.success(reservationService.complete(id, ownerId));
    }

    /**
     * 获取我的订单列表（作为预约用户）
     */
    @GetMapping("/my")
    public Result<Page<Reservation>> getMyOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Reservation> page = new Page<>(pageNum, pageSize);
        return Result.success(reservationService.getUserOrders(userId, status, page));
    }

    /**
     * 获取我收到的订单列表（作为车位主人）
     */
    @GetMapping("/received")
    public Result<Page<Reservation>> getReceivedOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long ownerId = (Long) request.getAttribute("userId");
        Page<Reservation> page = new Page<>(pageNum, pageSize);
        return Result.success(reservationService.getOwnerOrders(ownerId, status, page));
    }

    /**
     * 获取车位的订单列表
     */
    @GetMapping("/space/{spaceId}")
    public Result<Page<Reservation>> getSpaceOrders(
            @PathVariable Long spaceId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Reservation> page = new Page<>(pageNum, pageSize);
        return Result.success(reservationService.getSpaceOrders(spaceId, status, page));
    }

    /**
     * 检查时段是否可预约
     */
    @GetMapping("/check-available")
    public Result<Boolean> checkAvailable(
            @RequestParam Long spaceId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        boolean isBooked = reservationService.isTimeSlotBooked(spaceId, startTime, endTime, null);
        return Result.success(!isBooked);
    }

    /**
     * 获取我的订单统计（作为预约用户）
     */
    @GetMapping("/stats/user")
    public Result<ReservationService.ReservationStats> getUserStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(reservationService.getUserStats(userId));
    }

    /**
     * 获取我的订单统计（作为车位主人）
     */
    @GetMapping("/stats/owner")
    public Result<ReservationService.ReservationStats> getOwnerStats(HttpServletRequest request) {
        Long ownerId = (Long) request.getAttribute("userId");
        return Result.success(reservationService.getOwnerStats(ownerId));
    }

    // ========== 请求/响应 DTO ==========

    /**
     * 创建预约请求
     */
    public record CreateReservationRequest(
            Long spaceId,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime
    ) {}

    /**
     * 取消请求
     */
    public record CancelRequest(String reason) {}

    /**
     * 核销请求
     */
    public record VerifyRequest(String verifyCode) {}
}
