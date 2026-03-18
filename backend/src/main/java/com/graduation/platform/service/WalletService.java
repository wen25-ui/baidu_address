package com.graduation.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.model.entity.Transaction;
import com.graduation.platform.model.entity.Wallet;

import java.math.BigDecimal;

/**
 * 钱包服务接口
 */
public interface WalletService {

    /**
     * 获取用户钱包信息
     * @param userId 用户ID
     * @return 钱包信息
     */
    Wallet getByUserId(Long userId);

    /**
     * 获取或创建用户钱包
     * @param userId 用户ID
     * @return 钱包信息
     */
    Wallet getOrCreate(Long userId);

    /**
     * 充值
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 充值后余额
     */
    BigDecimal recharge(Long userId, BigDecimal amount);

    /**
     * 提现申请
     * @param userId 用户ID
     * @param amount 提现金额
     * @return 是否成功
     */
    boolean withdraw(Long userId, BigDecimal amount);

    /**
     * 冻结金额
     * @param userId 用户ID
     * @param amount 冻结金额
     * @return 是否成功
     */
    boolean freeze(Long userId, BigDecimal amount);

    /**
     * 解冻金额
     * @param userId 用户ID
     * @param amount 解冻金额
     * @return 是否成功
     */
    boolean unfreeze(Long userId, BigDecimal amount);

    /**
     * 扣款
     * @param userId 用户ID
     * @param amount 扣款金额
     * @param reservationId 关联订单ID
     * @param description 描述
     * @return 是否成功
     */
    boolean deduct(Long userId, BigDecimal amount, Long reservationId, String description);

    /**
     * 增加收入
     * @param userId 用户ID
     * @param amount 金额
     * @param reservationId 关联订单ID
     * @param description 描述
     * @return 是否成功
     */
    boolean addIncome(Long userId, BigDecimal amount, Long reservationId, String description);

    /**
     * 退款
     * @param userId 用户ID
     * @param amount 退款金额
     * @param reservationId 关联订单ID
     * @param description 描述
     * @return 是否成功
     */
    boolean refund(Long userId, BigDecimal amount, Long reservationId, String description);

    /**
     * 获取交易记录
     * @param userId 用户ID
     * @param type 交易类型（可选）
     * @param page 分页参数
     * @return 交易记录列表
     */
    Page<Transaction> getTransactions(Long userId, Integer type, Page<Transaction> page);

    /**
     * 获取钱包统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    WalletStats getStats(Long userId);

    /**
     * 钱包统计信息
     */
    record WalletStats(
        BigDecimal balance,
        BigDecimal frozenAmount,
        BigDecimal totalIncome,
        BigDecimal totalWithdraw,
        BigDecimal totalExpense,
        int transactionCount
    ) {}
}
