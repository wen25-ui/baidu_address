package com.graduation.platform.service;

import com.graduation.platform.model.entity.ParkingSpaceRule;

import java.time.LocalDate;
import java.util.List;

/**
 * 车位时段规则服务接口
 */
public interface ParkingSpaceRuleService {

    /**
     * 添加车位可用时段规则
     * @param rule 规则信息
     * @return 规则ID
     */
    Long addRule(ParkingSpaceRule rule);

    /**
     * 批量添加规则
     * @param rules 规则列表
     * @return 成功添加的数量
     */
    int batchAddRules(List<ParkingSpaceRule> rules);

    /**
     * 获取车位的所有规则
     * @param parkingSpaceId 车位ID
     * @return 规则列表
     */
    List<ParkingSpaceRule> getByParkingSpaceId(Long parkingSpaceId);

    /**
     * 获取指定日期的可用时段
     * @param parkingSpaceId 车位ID
     * @param date 日期
     * @return 规则列表
     */
    List<ParkingSpaceRule> getAvailableRules(Long parkingSpaceId, LocalDate date);

    /**
     * 更新规则
     * @param rule 规则信息
     * @return 是否成功
     */
    boolean updateRule(ParkingSpaceRule rule);

    /**
     * 删除规则
     * @param ruleId 规则ID
     * @param ownerId 车位主人ID（验证权限）
     * @return 是否成功
     */
    boolean deleteRule(Long ruleId, Long ownerId);

    /**
     * 删除车位的所有规则
     * @param parkingSpaceId 车位ID
     * @return 删除的数量
     */
    int deleteByParkingSpaceId(Long parkingSpaceId);

    /**
     * 检查时段是否可预约
     * @param parkingSpaceId 车位ID
     * @param date 日期
     * @param startHour 开始小时
     * @param endHour 结束小时
     * @return 是否可预约
     */
    boolean checkAvailable(Long parkingSpaceId, LocalDate date, int startHour, int endHour);
}
