package com.graduation.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 信用分变动记录实体类
 */
@Data
@TableName("credit_record")
public class CreditRecord {

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
     * 变动类型 1-完成订单 2-超时未核销 3-临时取消 4-被投诉 5-系统调整
     */
    private Integer changeType;

    /**
     * 变动分值（可正可负）
     */
    private Integer changeValue;

    /**
     * 变动前分数
     */
    private Integer beforeScore;

    /**
     * 变动后分数
     */
    private Integer afterScore;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
