package com.graduation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.BusinessException;
import com.graduation.platform.mapper.TransactionMapper;
import com.graduation.platform.mapper.WalletMapper;
import com.graduation.platform.model.entity.Transaction;
import com.graduation.platform.model.entity.Wallet;
import com.graduation.platform.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

    /**
     * 交易类型常量
     */
    private static final int TYPE_EXPENSE = 1;      // 支出
    private static final int TYPE_INCOME = 2;       // 收入
    private static final int TYPE_WITHDRAW = 3;     // 提现
    private static final int TYPE_REFUND = 4;       // 退款
    private static final int TYPE_RECHARGE = 5;     // 充值

    /**
     * 最小提现金额
     */
    private static final BigDecimal MIN_WITHDRAW_AMOUNT = new BigDecimal("10.00");

    @Override
    public Wallet getByUserId(Long userId) {
        return walletMapper.selectOne(
                new LambdaQueryWrapper<Wallet>().eq(Wallet::getUserId, userId)
        );
    }

    @Override
    public Wallet getOrCreate(Long userId) {
        Wallet wallet = getByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozenAmount(BigDecimal.ZERO);
            wallet.setTotalIncome(BigDecimal.ZERO);
            wallet.setTotalWithdraw(BigDecimal.ZERO);
            walletMapper.insert(wallet);
            log.info("创建用户钱包: userId={}", userId);
        }
        return wallet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal recharge(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        Wallet wallet = getOrCreate(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletMapper.updateById(wallet);

        // 记录交易流水
        recordTransaction(userId, null, TYPE_RECHARGE, amount, wallet.getBalance(), "账户充值");

        log.info("用户充值成功: userId={}, amount={}, balance={}", userId, amount, wallet.getBalance());
        return wallet.getBalance();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdraw(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(MIN_WITHDRAW_AMOUNT) < 0) {
            throw new BusinessException("提现金额不能少于" + MIN_WITHDRAW_AMOUNT + "元");
        }

        Wallet wallet = getOrCreate(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        // 扣除余额
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setTotalWithdraw(wallet.getTotalWithdraw().add(amount));
        walletMapper.updateById(wallet);

        // 记录交易流水
        recordTransaction(userId, null, TYPE_WITHDRAW, amount.negate(), wallet.getBalance(), "账户提现");

        log.info("用户提现成功: userId={}, amount={}, balance={}", userId, amount, wallet.getBalance());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freeze(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("冻结金额必须大于0");
        }

        Wallet wallet = getOrCreate(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setFrozenAmount(wallet.getFrozenAmount().add(amount));
        walletMapper.updateById(wallet);

        log.info("冻结金额成功: userId={}, amount={}", userId, amount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreeze(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("解冻金额必须大于0");
        }

        Wallet wallet = getOrCreate(userId);
        if (wallet.getFrozenAmount().compareTo(amount) < 0) {
            throw new BusinessException("冻结金额不足");
        }

        wallet.setFrozenAmount(wallet.getFrozenAmount().subtract(amount));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletMapper.updateById(wallet);

        log.info("解冻金额成功: userId={}, amount={}", userId, amount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deduct(Long userId, BigDecimal amount, Long reservationId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("扣款金额必须大于0");
        }

        Wallet wallet = getOrCreate(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletMapper.updateById(wallet);

        // 记录交易流水
        recordTransaction(userId, reservationId, TYPE_EXPENSE, amount.negate(), wallet.getBalance(), description);

        log.info("扣款成功: userId={}, amount={}, reservationId={}", userId, amount, reservationId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addIncome(Long userId, BigDecimal amount, Long reservationId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("收入金额必须大于0");
        }

        Wallet wallet = getOrCreate(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setTotalIncome(wallet.getTotalIncome().add(amount));
        walletMapper.updateById(wallet);

        // 记录交易流水
        recordTransaction(userId, reservationId, TYPE_INCOME, amount, wallet.getBalance(), description);

        log.info("增加收入成功: userId={}, amount={}, reservationId={}", userId, amount, reservationId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refund(Long userId, BigDecimal amount, Long reservationId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("退款金额必须大于0");
        }

        Wallet wallet = getOrCreate(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletMapper.updateById(wallet);

        // 记录交易流水
        recordTransaction(userId, reservationId, TYPE_REFUND, amount, wallet.getBalance(), description);

        log.info("退款成功: userId={}, amount={}, reservationId={}", userId, amount, reservationId);
        return true;
    }

    /**
     * 记录交易流水
     */
    private void recordTransaction(Long userId, Long reservationId, int type, 
                                   BigDecimal amount, BigDecimal balance, String description) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setReservationId(reservationId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalance(balance);
        transaction.setDescription(description);
        transactionMapper.insert(transaction);
    }

    @Override
    public Page<Transaction> getTransactions(Long userId, Integer type, Page<Transaction> page) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getUserId, userId);
        if (type != null) {
            wrapper.eq(Transaction::getType, type);
        }
        wrapper.orderByDesc(Transaction::getCreatedAt);
        return transactionMapper.selectPage(page, wrapper);
    }

    @Override
    public WalletStats getStats(Long userId) {
        Wallet wallet = getOrCreate(userId);

        // 计算总支出
        LambdaQueryWrapper<Transaction> expenseWrapper = new LambdaQueryWrapper<>();
        expenseWrapper.eq(Transaction::getUserId, userId)
                .eq(Transaction::getType, TYPE_EXPENSE);
        List<Transaction> expenses = transactionMapper.selectList(expenseWrapper);
        BigDecimal totalExpense = expenses.stream()
                .map(t -> t.getAmount().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 交易次数
        Long transactionCount = transactionMapper.selectCount(
                new LambdaQueryWrapper<Transaction>().eq(Transaction::getUserId, userId)
        );

        return new WalletStats(
                wallet.getBalance(),
                wallet.getFrozenAmount(),
                wallet.getTotalIncome(),
                wallet.getTotalWithdraw(),
                totalExpense,
                transactionCount.intValue()
        );
    }
}
