package com.graduation.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易流水实体类
 */
@Data
@TableName("transaction")
public class Transaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 关联预约ID
     */
    private Long reservationId;

    /**
     * 类型 1-支出 2-收入 3-提现 4-退款
     */
    private Integer type;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 变动后余额
     */
    private BigDecimal balance;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
