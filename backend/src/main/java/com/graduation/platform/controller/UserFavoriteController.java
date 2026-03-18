package com.graduation.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.Result;
import com.graduation.platform.model.entity.ParkingSpace;
import com.graduation.platform.service.UserFavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户收藏控制器
 */
@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/{spaceId}")
    public Result<Long> add(@PathVariable Long spaceId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userFavoriteService.add(userId, spaceId));
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{spaceId}")
    public Result<Boolean> remove(@PathVariable Long spaceId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userFavoriteService.remove(userId, spaceId));
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/{spaceId}/check")
    public Result<Boolean> check(@PathVariable Long spaceId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userFavoriteService.isFavorite(userId, spaceId));
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/list")
    public Result<Page<ParkingSpace>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        return Result.success(userFavoriteService.getFavorites(userId, page));
    }

    /**
     * 获取收藏数量
     */
    @GetMapping("/count")
    public Result<Integer> count(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userFavoriteService.getFavoriteCount(userId));
    }
}
