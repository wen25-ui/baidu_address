package com.graduation.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 车位可用时段规则实体类
 */
@Data
@TableName("parking_space_rule")
public class ParkingSpaceRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车位ID
     */
    private Long spaceId;

    /**
     * 规则类型 1-每周重复 2-单次设置
     */
    private Integer ruleType;

    /**
     * 周几 1-7，多个用逗号分隔
     */
    private String dayOfWeek;

    /**
     * 特定日期（单次设置时使用）
     */
    private LocalDate specificDate;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 是否可用 0-不可用 1-可用
     */
    private Integer isAvailable;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
