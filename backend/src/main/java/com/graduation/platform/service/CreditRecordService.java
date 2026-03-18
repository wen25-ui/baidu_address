package com.graduation.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.model.entity.CreditRecord;

/**
 * 信用分服务接口
 */
public interface CreditRecordService {

    /**
     * 完成订单加分
     * @param userId 用户ID
     * @param reservationId 订单ID
     * @return 变动后分数
     */
    int addScoreForCompletion(Long userId, Long reservationId);

    /**
     * 超时未核销扣分
     * @param userId 用户ID
     * @param reservationId 订单ID
     * @return 变动后分数
     */
    int deductScoreForTimeout(Long userId, Long reservationId);

    /**
     * 临时取消扣分
     * @param userId 用户ID
     * @param reservationId 订单ID
     * @return 变动后分数
     */
    int deductScoreForCancel(Long userId, Long reservationId);

    /**
     * 被投诉扣分
     * @param userId 用户ID
     * @param reservationId 订单ID
     * @param remark 备注
     * @return 变动后分数
     */
    int deductScoreForComplaint(Long userId, Long reservationId, String remark);

    /**
     * 系统调整分数
     * @param userId 用户ID
     * @param changeValue 变动值（正负）
     * @param remark 备注
     * @return 变动后分数
     */
    int adjustScore(Long userId, int changeValue, String remark);

    /**
     * 获取用户当前信用分
     * @param userId 用户ID
     * @return 当前信用分
     */
    int getCurrentScore(Long userId);

    /**
     * 获取用户信用记录
     * @param userId 用户ID
     * @param page 分页参数
     * @return 信用记录列表
     */
    Page<CreditRecord> getRecords(Long userId, Page<CreditRecord> page);

    /**
     * 检查用户信用是否满足要求
     * @param userId 用户ID
     * @param requiredScore 要求的最低分数
     * @return 是否满足
     */
    boolean checkCredit(Long userId, int requiredScore);

    /**
     * 信用分变动类型
     */
    interface ChangeType {
        int COMPLETE_ORDER = 1;     // 完成订单
        int TIMEOUT = 2;            // 超时未核销
        int CANCEL = 3;             // 临时取消
        int COMPLAINT = 4;          // 被投诉
        int SYSTEM_ADJUST = 5;      // 系统调整
    }
}
