package com.graduation.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车位实体类
 */
@Data
@TableName("parking_space")
public class ParkingSpace {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车位主人ID
     */
    private Long ownerId;

    /**
     * 车位标题
     */
    private String title;

    /**
     * 车位描述
     */
    private String description;

    /**
     * 小区名称
     */
    private String communityName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 车位编号
     */
    private String spaceNumber;

    /**
     * 每小时价格
     */
    private BigDecimal pricePerHour;

    /**
     * 车位图片URLs，JSON数组
     */
    private String images;

    /**
     * 状态 0-待审核 1-已上架 2-已下架 3-审核拒绝
     */
    private Integer status;

    /**
     * 审核拒绝原因
     */
    private String rejectReason;

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
