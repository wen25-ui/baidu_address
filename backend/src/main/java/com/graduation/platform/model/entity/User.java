package com.graduation.platform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * User entity.
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * WeChat openid.
     */
    private String openid;

    /**
     * Username for password login.
     */
    private String username;

    /**
     * BCrypt encoded password.
     */
    @JsonIgnore
    private String password;

    /**
     * Phone number.
     */
    private String phone;

    /**
     * Nickname.
     */
    private String nickname;

    /**
     * Avatar URL.
     */
    private String avatar;

    /**
     * Real name.
     */
    private String realName;

    /**
     * ID card.
     */
    private String idCard;

    /**
     * Whether user is real-name verified. 0-no, 1-yes.
     */
    private Integer isVerified;

    /**
     * Credit score.
     */
    private Integer creditScore;

    /**
     * Whether user can publish parking spaces. 0-no, 1-yes.
     */
    private Integer isOwner;

    /**
     * Status. 0-disabled, 1-active.
     */
    private Integer status;

    /**
     * Logical delete flag. 0-not deleted, 1-deleted.
     */
    @TableLogic
    private Integer deleted;

    /**
     * Created time.
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * Updated time.
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
