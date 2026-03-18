package com.graduation.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预约订单实体类
 */
@Data
@TableName("reservation")
public class Reservation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 预约用户ID
     */
    private Long userId;

    /**
     * 车位ID
     */
    private Long spaceId;

    /**
     * 车位主人ID
     */
    private Long ownerId;

    /**
     * 预约开始时间
     */
    private LocalDateTime startTime;

    /**
     * 预约结束时间
     */
    private LocalDateTime endTime;

    /**
     * 时长（小时）
     */
    private BigDecimal duration;

    /**
     * 单价
     */
    private BigDecimal pricePerHour;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 状态 0-待支付 1-待使用 2-使用中 3-已完成 4-已取消 5-已超时
     */
    private Integer status;

    /**
     * 核销验证码
     */
    private String verifyCode;

    /**
     * 核销时间
     */
    private LocalDateTime verifiedAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 取消时间
     */
    private LocalDateTime cancelledAt;

    /**
     * 取消原因
     */
    private String cancelReason;

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

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
