package com.graduation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.BusinessException;
import com.graduation.platform.mapper.ParkingSpaceMapper;
import com.graduation.platform.mapper.ReservationMapper;
import com.graduation.platform.mapper.TransactionMapper;
import com.graduation.platform.mapper.WalletMapper;
import com.graduation.platform.model.entity.ParkingSpace;
import com.graduation.platform.model.entity.Reservation;
import com.graduation.platform.model.entity.Transaction;
import com.graduation.platform.model.entity.Wallet;
import com.graduation.platform.service.ReservationService;
import com.graduation.platform.utils.OrderNoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

    /**
     * 订单状态常量
     */
    private static final int STATUS_PENDING_PAY = 0;    // 待支付
    private static final int STATUS_PENDING_USE = 1;    // 待使用
    private static final int STATUS_USING = 2;          // 使用中
    private static final int STATUS_COMPLETED = 3;      // 已完成
    private static final int STATUS_CANCELLED = 4;      // 已取消
    private static final int STATUS_TIMEOUT = 5;        // 已超时

    /**
     * 交易类型常量
     */
    private static final int TRANSACTION_EXPENSE = 1;   // 支出
    private static final int TRANSACTION_INCOME = 2;    // 收入
    private static final int TRANSACTION_REFUND = 4;    // 退款

    /**
     * 支付超时时间（分钟）
     */
    private static final int PAY_TIMEOUT_MINUTES = 15;

    /**
     * 平台服务费比例
     */
    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.10");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation create(Long userId, Long spaceId, LocalDateTime startTime, LocalDateTime endTime) {
        // 参数校验
        if (startTime == null || endTime == null) {
            throw new BusinessException("预约时间不能为空");
        }
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }

        // 获取车位信息
        ParkingSpace parkingSpace = parkingSpaceMapper.selectById(spaceId);
        if (parkingSpace == null) {
            throw new BusinessException("车位不存在");
        }
        if (parkingSpace.getStatus() != 1) {
            throw new BusinessException("车位未上架，无法预约");
        }
        if (parkingSpace.getOwnerId().equals(userId)) {
            throw new BusinessException("不能预约自己的车位");
        }

        // 检查时段是否已被预约
        if (isTimeSlotBooked(spaceId, startTime, endTime, null)) {
            throw new BusinessException("该时段已被预约");
        }

        // 计算时长和金额
        Duration duration = Duration.between(startTime, endTime);
        BigDecimal hours = BigDecimal.valueOf(duration.toMinutes())
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = parkingSpace.getPricePerHour().multiply(hours)
                .setScale(2, RoundingMode.HALF_UP);

        // 创建预约订单
        Reservation reservation = new Reservation();
        reservation.setOrderNo(OrderNoUtils.generateOrderNo());
        reservation.setUserId(userId);
        reservation.setSpaceId(spaceId);
        reservation.setOwnerId(parkingSpace.getOwnerId());
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setDuration(hours);
        reservation.setPricePerHour(parkingSpace.getPricePerHour());
        reservation.setTotalAmount(totalAmount);
        reservation.setStatus(STATUS_PENDING_PAY);
        reservation.setVerifyCode(OrderNoUtils.generateVerifyCode());

        reservationMapper.insert(reservation);
        log.info("创建预约订单成功: orderNo={}, userId={}, spaceId={}", 
                reservation.getOrderNo(), userId, spaceId);

        return reservation;
    }

    @Override
    public Reservation getById(Long id) {
        return reservationMapper.selectById(id);
    }

    @Override
    public Reservation getByOrderNo(String orderNo) {
        return reservationMapper.selectOne(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getOrderNo, orderNo)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pay(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("订单不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (reservation.getStatus() != STATUS_PENDING_PAY) {
            throw new BusinessException("订单状态不正确");
        }

        // 检查是否超时
        if (reservation.getCreatedAt().plusMinutes(PAY_TIMEOUT_MINUTES).isBefore(LocalDateTime.now())) {
            // 标记为超时
            reservation.setStatus(STATUS_TIMEOUT);
            reservation.setCancelledAt(LocalDateTime.now());
            reservation.setCancelReason("支付超时");
            reservationMapper.updateById(reservation);
            throw new BusinessException("订单已超时，请重新预约");
        }

        // 获取用户钱包
        Wallet userWallet = getOrCreateWallet(userId);
        if (userWallet.getBalance().compareTo(reservation.getTotalAmount()) < 0) {
            throw new BusinessException("余额不足");
        }

        // 扣除用户余额
        userWallet.setBalance(userWallet.getBalance().subtract(reservation.getTotalAmount()));
        walletMapper.updateById(userWallet);

        // 记录支出流水
        Transaction expense = new Transaction();
        expense.setUserId(userId);
        expense.setReservationId(id);
        expense.setType(TRANSACTION_EXPENSE);
        expense.setAmount(reservation.getTotalAmount().negate());
        expense.setBalance(userWallet.getBalance());
        expense.setDescription("车位预约支付 - " + reservation.getOrderNo());
        transactionMapper.insert(expense);

        // 更新订单状态
        reservation.setStatus(STATUS_PENDING_USE);
        reservationMapper.updateById(reservation);

        log.info("订单支付成功: orderNo={}, amount={}", reservation.getOrderNo(), reservation.getTotalAmount());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Long id, Long userId, String reason) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("订单不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 只有待支付和待使用状态可以取消
        if (reservation.getStatus() != STATUS_PENDING_PAY && reservation.getStatus() != STATUS_PENDING_USE) {
            throw new BusinessException("当前订单状态无法取消");
        }

        // 如果已支付，执行退款
        if (reservation.getStatus() == STATUS_PENDING_USE) {
            processRefund(reservation);
        }

        // 更新订单状态
        reservation.setStatus(STATUS_CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancelReason(reason != null ? reason : "用户取消");
        reservationMapper.updateById(reservation);

        log.info("订单取消成功: orderNo={}, reason={}", reservation.getOrderNo(), reason);
        return true;
    }

    /**
     * 处理退款
     */
    private void processRefund(Reservation reservation) {
        // 获取用户钱包并退款
        Wallet userWallet = getOrCreateWallet(reservation.getUserId());
        userWallet.setBalance(userWallet.getBalance().add(reservation.getTotalAmount()));
        walletMapper.updateById(userWallet);

        // 记录退款流水
        Transaction refund = new Transaction();
        refund.setUserId(reservation.getUserId());
        refund.setReservationId(reservation.getId());
        refund.setType(TRANSACTION_REFUND);
        refund.setAmount(reservation.getTotalAmount());
        refund.setBalance(userWallet.getBalance());
        refund.setDescription("车位预约退款 - " + reservation.getOrderNo());
        transactionMapper.insert(refund);

        log.info("订单退款成功: orderNo={}, amount={}", reservation.getOrderNo(), reservation.getTotalAmount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verify(Long id, String verifyCode, Long ownerId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("订单不存在");
        }
        if (!reservation.getOwnerId().equals(ownerId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (reservation.getStatus() != STATUS_PENDING_USE) {
            throw new BusinessException("订单状态不正确");
        }
        if (!reservation.getVerifyCode().equals(verifyCode)) {
            throw new BusinessException("核销码错误");
        }

        // 更新订单状态为使用中
        reservation.setStatus(STATUS_USING);
        reservation.setVerifiedAt(LocalDateTime.now());
        reservationMapper.updateById(reservation);

        log.info("订单核销成功: orderNo={}", reservation.getOrderNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean complete(Long id, Long ownerId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("订单不存在");
        }
        if (!reservation.getOwnerId().equals(ownerId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (reservation.getStatus() != STATUS_USING) {
            throw new BusinessException("订单状态不正确，只有使用中的订单可以完成");
        }

        // 结算给车位主人（扣除平台服务费）
        settleToOwner(reservation);

        // 更新订单状态
        reservation.setStatus(STATUS_COMPLETED);
        reservation.setCompletedAt(LocalDateTime.now());
        reservationMapper.updateById(reservation);

        log.info("订单完成: orderNo={}", reservation.getOrderNo());
        return true;
    }

    /**
     * 结算给车位主人
     */
    private void settleToOwner(Reservation reservation) {
        // 计算车位主人收入（扣除平台服务费）
        BigDecimal platformFee = reservation.getTotalAmount().multiply(PLATFORM_FEE_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal ownerIncome = reservation.getTotalAmount().subtract(platformFee);

        // 获取车位主人钱包
        Wallet ownerWallet = getOrCreateWallet(reservation.getOwnerId());
        ownerWallet.setBalance(ownerWallet.getBalance().add(ownerIncome));
        ownerWallet.setTotalIncome(ownerWallet.getTotalIncome().add(ownerIncome));
        walletMapper.updateById(ownerWallet);

        // 记录收入流水
        Transaction income = new Transaction();
        income.setUserId(reservation.getOwnerId());
        income.setReservationId(reservation.getId());
        income.setType(TRANSACTION_INCOME);
        income.setAmount(ownerIncome);
        income.setBalance(ownerWallet.getBalance());
        income.setDescription("车位出租收入 - " + reservation.getOrderNo() + " (扣除" + 
                PLATFORM_FEE_RATE.multiply(BigDecimal.valueOf(100)).intValue() + "%服务费)");
        transactionMapper.insert(income);

        log.info("订单结算成功: orderNo={}, ownerIncome={}, platformFee={}", 
                reservation.getOrderNo(), ownerIncome, platformFee);
    }

    /**
     * 获取或创建钱包
     */
    private Wallet getOrCreateWallet(Long userId) {
        Wallet wallet = walletMapper.selectOne(
                new LambdaQueryWrapper<Wallet>().eq(Wallet::getUserId, userId)
        );
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozenAmount(BigDecimal.ZERO);
            wallet.setTotalIncome(BigDecimal.ZERO);
            wallet.setTotalWithdraw(BigDecimal.ZERO);
            walletMapper.insert(wallet);
        }
        return wallet;
    }

    @Override
    public Page<Reservation> getUserOrders(Long userId, Integer status, Page<Reservation> page) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, userId);
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreatedAt);
        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<Reservation> getOwnerOrders(Long ownerId, Integer status, Page<Reservation> page) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getOwnerId, ownerId);
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreatedAt);
        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<Reservation> getSpaceOrders(Long spaceId, Integer status, Page<Reservation> page) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getSpaceId, spaceId);
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreatedAt);
        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean isTimeSlotBooked(Long spaceId, LocalDateTime startTime, LocalDateTime endTime, Long excludeReservationId) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getSpaceId, spaceId)
                // 只考虑有效订单（待支付、待使用、使用中）
                .in(Reservation::getStatus, STATUS_PENDING_PAY, STATUS_PENDING_USE, STATUS_USING)
                // 时间段重叠判断：开始时间 < 查询结束时间 AND 结束时间 > 查询开始时间
                .lt(Reservation::getStartTime, endTime)
                .gt(Reservation::getEndTime, startTime);

        if (excludeReservationId != null) {
            wrapper.ne(Reservation::getId, excludeReservationId);
        }

        return reservationMapper.selectCount(wrapper) > 0;
    }

    @Override
    public ReservationStats getUserStats(Long userId) {
        return getStats(userId, true);
    }

    @Override
    public ReservationStats getOwnerStats(Long ownerId) {
        return getStats(ownerId, false);
    }

    /**
     * 获取统计信息
     */
    private ReservationStats getStats(Long id, boolean isUser) {
        LambdaQueryWrapper<Reservation> baseWrapper = new LambdaQueryWrapper<>();
        if (isUser) {
            baseWrapper.eq(Reservation::getUserId, id);
        } else {
            baseWrapper.eq(Reservation::getOwnerId, id);
        }

        List<Reservation> reservations = reservationMapper.selectList(baseWrapper);

        int totalCount = reservations.size();
        int pendingPayCount = 0;
        int pendingUseCount = 0;
        int usingCount = 0;
        int completedCount = 0;
        int cancelledCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal completedAmount = BigDecimal.ZERO;

        for (Reservation r : reservations) {
            totalAmount = totalAmount.add(r.getTotalAmount());
            switch (r.getStatus()) {
                case STATUS_PENDING_PAY -> pendingPayCount++;
                case STATUS_PENDING_USE -> pendingUseCount++;
                case STATUS_USING -> usingCount++;
                case STATUS_COMPLETED -> {
                    completedCount++;
                    completedAmount = completedAmount.add(r.getTotalAmount());
                }
                case STATUS_CANCELLED, STATUS_TIMEOUT -> cancelledCount++;
            }
        }

        return new ReservationStats(
                totalCount, pendingPayCount, pendingUseCount, usingCount,
                completedCount, cancelledCount, totalAmount, completedAmount
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleTimeoutOrders() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(PAY_TIMEOUT_MINUTES);
        
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getStatus, STATUS_PENDING_PAY)
                .lt(Reservation::getCreatedAt, timeoutThreshold);

        List<Reservation> timeoutOrders = reservationMapper.selectList(wrapper);
        
        for (Reservation order : timeoutOrders) {
            order.setStatus(STATUS_TIMEOUT);
            order.setCancelledAt(LocalDateTime.now());
            order.setCancelReason("支付超时自动取消");
            reservationMapper.updateById(order);
            log.info("订单超时取消: orderNo={}", order.getOrderNo());
        }

        return timeoutOrders.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoCompleteOrders() {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getStatus, STATUS_USING)
                .lt(Reservation::getEndTime, LocalDateTime.now());

        List<Reservation> ordersToComplete = reservationMapper.selectList(wrapper);
        
        for (Reservation order : ordersToComplete) {
            // 结算给车位主人
            settleToOwner(order);
            
            // 更新订单状态
            order.setStatus(STATUS_COMPLETED);
            order.setCompletedAt(LocalDateTime.now());
            reservationMapper.updateById(order);
            log.info("订单自动完成: orderNo={}", order.getOrderNo());
        }

        return ordersToComplete.size();
    }
}
