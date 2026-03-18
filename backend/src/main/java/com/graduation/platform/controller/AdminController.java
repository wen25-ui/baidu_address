package com.graduation.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.Result;
import com.graduation.platform.model.entity.Admin;
import com.graduation.platform.model.entity.ParkingSpace;
import com.graduation.platform.model.entity.Reservation;
import com.graduation.platform.model.entity.User;
import com.graduation.platform.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 管理端控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ========== 认证相关 ==========

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = adminService.login(request.username(), request.password());
        return Result.success(new LoginResponse(token));
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/info")
    public Result<Admin> getInfo(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        return Result.success(adminService.getById(adminId));
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Boolean> changePassword(@RequestBody ChangePasswordRequest request,
                                           HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("userId");
        return Result.success(adminService.changePassword(adminId, request.oldPassword(), request.newPassword()));
    }

    // ========== 用户管理 ==========

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public Result<Page<User>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return Result.success(adminService.getUserList(keyword, status, page));
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{userId}/status")
    public Result<Boolean> updateUserStatus(@PathVariable Long userId,
                                             @RequestBody UpdateStatusRequest request) {
        return Result.success(adminService.updateUserStatus(userId, request.status()));
    }

    // ========== 车位管理 ==========

    /**
     * 获取车位列表
     */
    @GetMapping("/parking-spaces")
    public Result<Page<ParkingSpace>> getParkingSpaceList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        return Result.success(adminService.getParkingSpaceList(keyword, status, page));
    }

    /**
     * 审核车位
     */
    @PostMapping("/parking-spaces/{spaceId}/audit")
    public Result<Boolean> auditParkingSpace(@PathVariable Long spaceId,
                                              @RequestBody AuditRequest request) {
        return Result.success(adminService.auditParkingSpace(spaceId, request.approved(), request.rejectReason()));
    }

    /**
     * 强制下架车位
     */
    @PostMapping("/parking-spaces/{spaceId}/force-offline")
    public Result<Boolean> forceParkingSpaceOffline(@PathVariable Long spaceId,
                                                     @RequestBody ForceOfflineRequest request) {
        return Result.success(adminService.forceParkingSpaceOffline(spaceId, request.reason()));
    }

    // ========== 订单管理 ==========

    /**
     * 获取订单列表
     */
    @GetMapping("/reservations")
    public Result<Page<Reservation>> getReservationList(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Reservation> page = new Page<>(pageNum, pageSize);
        return Result.success(adminService.getReservationList(orderNo, status, page));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/reservations/{id}")
    public Result<Reservation> getReservationDetail(@PathVariable Long id) {
        return Result.success(adminService.getReservationDetail(id));
    }

    // ========== 数据统计 ==========

    /**
     * 获取平台统计数据
     */
    @GetMapping("/stats/platform")
    public Result<AdminService.PlatformStats> getPlatformStats() {
        return Result.success(adminService.getPlatformStats());
    }

    /**
     * 获取收入统计
     */
    @GetMapping("/stats/income")
    public Result<AdminService.IncomeStats> getIncomeStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(adminService.getIncomeStats(startDate, endDate));
    }

    // ========== 请求/响应 DTO ==========

    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String token) {}
    public record ChangePasswordRequest(String oldPassword, String newPassword) {}
    public record UpdateStatusRequest(Integer status) {}
    public record AuditRequest(boolean approved, String rejectReason) {}
    public record ForceOfflineRequest(String reason) {}
}
