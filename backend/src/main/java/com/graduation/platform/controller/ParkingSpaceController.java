package com.graduation.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.Result;
import com.graduation.platform.model.entity.ParkingSpace;
import com.graduation.platform.model.entity.ParkingSpaceRule;
import com.graduation.platform.service.ParkingSpaceRuleService;
import com.graduation.platform.service.ParkingSpaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 车位管理控制器
 */
@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;
    private final ParkingSpaceRuleService ruleService;

    /**
     * 发布车位
     */
    @PostMapping
    public Result<Long> publish(@RequestBody ParkingSpace parkingSpace, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        parkingSpace.setOwnerId(userId);
        Long id = parkingSpaceService.publish(parkingSpace);
        return Result.success(id);
    }

    /**
     * 获取车位详情
     */
    @GetMapping("/{id}")
    public Result<ParkingSpace> getById(@PathVariable Long id) {
        return Result.success(parkingSpaceService.getById(id));
    }

    /**
     * 更新车位信息
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ParkingSpace parkingSpace,
                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ParkingSpace existing = parkingSpaceService.getById(id);
        if (!existing.getOwnerId().equals(userId)) {
            return Result.error(403, "No permission");
        }
        parkingSpace.setId(id);
        return Result.success(parkingSpaceService.update(parkingSpace));
    }

    /**
     * 删除车位
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(parkingSpaceService.delete(id, userId));
    }

    /**
     * 上架车位
     */
    @PostMapping("/{id}/online")
    public Result<Boolean> online(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(parkingSpaceService.online(id, userId));
    }

    /**
     * 下架车位
     */
    @PostMapping("/{id}/offline")
    public Result<Boolean> offline(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(parkingSpaceService.offline(id, userId));
    }

    /**
     * 搜索附近车位
     */
    @GetMapping("/nearby")
    public Result<Page<ParkingSpace>> searchNearby(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "3") Double radius,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        return Result.success(parkingSpaceService.searchNearby(latitude, longitude, radius, page));
    }

    /**
     * 关键词搜索车位
     */
    @GetMapping("/search")
    public Result<Page<ParkingSpace>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        return Result.success(parkingSpaceService.searchByKeyword(keyword, page));
    }

    /**
     * 获取我的车位列表
     */
    @GetMapping("/mine")
    public Result<Page<ParkingSpace>> getMySpaces(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        return Result.success(parkingSpaceService.getByOwnerId(userId, page));
    }

    /**
     * 获取车位统计
     */
    @GetMapping("/stats")
    public Result<ParkingSpaceService.ParkingSpaceStats> getStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(parkingSpaceService.getStats(userId));
    }

    // ========== 时段规则管理 ==========

    /**
     * 添加车位可用时段规则
     */
    @PostMapping("/{spaceId}/rules")
    public Result<Long> addRule(@PathVariable Long spaceId, @RequestBody ParkingSpaceRule rule,
                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ParkingSpace space = parkingSpaceService.getById(spaceId);
        if (!space.getOwnerId().equals(userId)) {
            return Result.error(403, "No permission");
        }
        rule.setSpaceId(spaceId);
        return Result.success(ruleService.addRule(rule));
    }

    /**
     * 获取车位的时段规则
     */
    @GetMapping("/{spaceId}/rules")
    public Result<List<ParkingSpaceRule>> getRules(@PathVariable Long spaceId) {
        return Result.success(ruleService.getByParkingSpaceId(spaceId));
    }

    /**
     * 获取指定日期的可用时段
     */
    @GetMapping("/{spaceId}/available")
    public Result<List<ParkingSpaceRule>> getAvailableRules(
            @PathVariable Long spaceId,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return Result.success(ruleService.getAvailableRules(spaceId, localDate));
    }

    /**
     * 删除时段规则
     */
    @DeleteMapping("/rules/{ruleId}")
    public Result<Boolean> deleteRule(@PathVariable Long ruleId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(ruleService.deleteRule(ruleId, userId));
    }
}