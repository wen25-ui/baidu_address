package com.graduation.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.Result;
import com.graduation.platform.model.entity.Transaction;
import com.graduation.platform.model.entity.Wallet;
import com.graduation.platform.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 钱包控制器
 */
@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * 获取钱包信息
     */
    @GetMapping
    public Result<Wallet> getWallet(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(walletService.getOrCreate(userId));
    }

    /**
     * 充值
     */
    @PostMapping("/recharge")
    public Result<BigDecimal> recharge(@RequestBody RechargeRequest rechargeRequest,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BigDecimal balance = walletService.recharge(userId, rechargeRequest.amount());
        return Result.success(balance);
    }

    /**
     * 提现
     */
    @PostMapping("/withdraw")
    public Result<Boolean> withdraw(@RequestBody WithdrawRequest withdrawRequest,
                                     HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(walletService.withdraw(userId, withdrawRequest.amount()));
    }

    /**
     * 获取交易记录
     */
    @GetMapping("/transactions")
    public Result<Page<Transaction>> getTransactions(
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Transaction> page = new Page<>(pageNum, pageSize);
        return Result.success(walletService.getTransactions(userId, type, page));
    }

    /**
     * 获取钱包统计
     */
    @GetMapping("/stats")
    public Result<WalletService.WalletStats> getStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(walletService.getStats(userId));
    }

    // ========== 请求DTO ==========

    public record RechargeRequest(BigDecimal amount) {}
    public record WithdrawRequest(BigDecimal amount) {}
}
