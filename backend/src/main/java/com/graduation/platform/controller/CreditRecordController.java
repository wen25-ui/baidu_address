package com.graduation.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.Result;
import com.graduation.platform.model.entity.CreditRecord;
import com.graduation.platform.service.CreditRecordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 信用分控制器
 */
@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CreditRecordController {

    private final CreditRecordService creditRecordService;

    /**
     * 获取当前信用分
     */
    @GetMapping("/score")
    public Result<Integer> getScore(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(creditRecordService.getCurrentScore(userId));
    }

    /**
     * 获取信用记录
     */
    @GetMapping("/records")
    public Result<Page<CreditRecord>> getRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<CreditRecord> page = new Page<>(pageNum, pageSize);
        return Result.success(creditRecordService.getRecords(userId, page));
    }

    /**
     * 检查信用是否满足要求
     */
    @GetMapping("/check")
    public Result<Boolean> checkCredit(
            @RequestParam(defaultValue = "60") Integer requiredScore,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(creditRecordService.checkCredit(userId, requiredScore));
    }
}
