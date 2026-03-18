package com.graduation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.BusinessException;
import com.graduation.platform.mapper.AdminMapper;
import com.graduation.platform.mapper.ParkingSpaceMapper;
import com.graduation.platform.mapper.ReservationMapper;
import com.graduation.platform.mapper.UserMapper;
import com.graduation.platform.model.entity.*;
import com.graduation.platform.service.AdminService;
import com.graduation.platform.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final ReservationMapper reservationMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     * 平台服务费比例
     */
    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.10");

    @Override
    public String login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名和密码不能为空");
        }

        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, username)
        );

        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (admin.getStatus() != 1) {
            throw new BusinessException("账户已被禁用");
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 更新最后登录时间
        admin.setLastLoginAt(LocalDateTime.now());
        adminMapper.updateById(admin);

        // 生成JWT
        String token = jwtUtils.generateToken(admin.getId(), "admin");
        log.info("管理员登录成功: username={}", username);
        return token;
    }

    @Override
    public Admin getById(Long adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin != null) {
            admin.setPassword(null); // 不返回密码
        }
        return admin;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminMapper.updateById(admin);

        log.info("管理员修改密码成功: adminId={}", adminId);
        return true;
    }

    // ========== 用户管理 ==========

    @Override
    public Page<User> getUserList(String keyword, Integer status, Page<User> page) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword)
            );
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        userMapper.updateById(user);

        log.info("更新用户状态: userId={}, status={}", userId, status);
        return true;
    }

    // ========== 车位管理 ==========

    @Override
    public Page<ParkingSpace> getParkingSpaceList(String keyword, Integer status, Page<ParkingSpace> page) {
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(ParkingSpace::getTitle, keyword)
                    .or()
                    .like(ParkingSpace::getCommunityName, keyword)
                    .or()
                    .like(ParkingSpace::getAddress, keyword)
            );
        }
        if (status != null) {
            wrapper.eq(ParkingSpace::getStatus, status);
        }
        wrapper.orderByDesc(ParkingSpace::getCreatedAt);
        return parkingSpaceMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditParkingSpace(Long spaceId, boolean approved, String rejectReason) {
        ParkingSpace space = parkingSpaceMapper.selectById(spaceId);
        if (space == null) {
            throw new BusinessException("车位不存在");
        }

        if (space.getStatus() != 0) {
            throw new BusinessException("车位不在待审核状态");
        }

        if (approved) {
            space.setStatus(1); // 已上架
            space.setRejectReason(null);
        } else {
            space.setStatus(3); // 审核拒绝
            space.setRejectReason(rejectReason);
        }
        parkingSpaceMapper.updateById(space);

        log.info("车位审核完成: spaceId={}, approved={}", spaceId, approved);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean forceParkingSpaceOffline(Long spaceId, String reason) {
        ParkingSpace space = parkingSpaceMapper.selectById(spaceId);
        if (space == null) {
            throw new BusinessException("车位不存在");
        }

        space.setStatus(2); // 已下架
        space.setRejectReason("管理员下架: " + reason);
        parkingSpaceMapper.updateById(space);

        log.info("强制下架车位: spaceId={}, reason={}", spaceId, reason);
        return true;
    }

    // ========== 订单管理 ==========

    @Override
    public Page<Reservation> getReservationList(String orderNo, Integer status, Page<Reservation> page) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(Reservation::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreatedAt);
        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    public Reservation getReservationDetail(Long reservationId) {
        return reservationMapper.selectById(reservationId);
    }

    // ========== 数据统计 ==========

    @Override
    public PlatformStats getPlatformStats() {
        // 用户统计
        long totalUsers = userMapper.selectCount(null);
        long activeUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 1)
        );

        // 车位统计
        long totalParkingSpaces = parkingSpaceMapper.selectCount(null);
        long onlineParkingSpaces = parkingSpaceMapper.selectCount(
                new LambdaQueryWrapper<ParkingSpace>().eq(ParkingSpace::getStatus, 1)
        );

        // 订单统计
        long totalReservations = reservationMapper.selectCount(null);
        long completedReservations = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>().eq(Reservation::getStatus, 3)
        );

        // 收入统计
        List<Reservation> completedOrders = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>().eq(Reservation::getStatus, 3)
        );
        BigDecimal totalRevenue = completedOrders.stream()
                .map(Reservation::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal platformRevenue = totalRevenue.multiply(PLATFORM_FEE_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        return new PlatformStats(
                totalUsers, activeUsers,
                totalParkingSpaces, onlineParkingSpaces,
                totalReservations, completedReservations,
                totalRevenue, platformRevenue
        );
    }

    @Override
    public IncomeStats getIncomeStats(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getStatus, 3); // 已完成订单
        
        if (startDate != null) {
            wrapper.ge(Reservation::getCompletedAt, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(Reservation::getCompletedAt, endDate.atTime(LocalTime.MAX));
        }

        List<Reservation> orders = reservationMapper.selectList(wrapper);
        
        BigDecimal totalAmount = orders.stream()
                .map(Reservation::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal platformFee = totalAmount.multiply(PLATFORM_FEE_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        
        int orderCount = orders.size();
        
        BigDecimal avgOrderAmount = orderCount > 0 
                ? totalAmount.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new IncomeStats(totalAmount, platformFee, orderCount, avgOrderAmount);
    }
}
