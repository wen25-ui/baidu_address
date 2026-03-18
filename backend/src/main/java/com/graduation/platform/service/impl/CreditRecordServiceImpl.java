package com.graduation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.BusinessException;
import com.graduation.platform.mapper.CreditRecordMapper;
import com.graduation.platform.mapper.UserMapper;
import com.graduation.platform.model.entity.CreditRecord;
import com.graduation.platform.model.entity.User;
import com.graduation.platform.service.CreditRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 信用分服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditRecordServiceImpl implements CreditRecordService {

    private final CreditRecordMapper creditRecordMapper;
    private final UserMapper userMapper;

    /**
     * 默认信用分
     */
    private static final int DEFAULT_SCORE = 100;

    /**
     * 最低信用分
     */
    private static final int MIN_SCORE = 0;

    /**
     * 最高信用分
     */
    private static final int MAX_SCORE = 150;

    /**
     * 完成订单加分
     */
    private static final int COMPLETE_ORDER_SCORE = 2;

    /**
     * 超时扣分
     */
    private static final int TIMEOUT_SCORE = -5;

    /**
     * 取消扣分
     */
    private static final int CANCEL_SCORE = -3;

    /**
     * 投诉扣分
     */
    private static final int COMPLAINT_SCORE = -10;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addScoreForCompletion(Long userId, Long reservationId) {
        return changeScore(userId, reservationId, ChangeType.COMPLETE_ORDER, COMPLETE_ORDER_SCORE, "完成订单奖励");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deductScoreForTimeout(Long userId, Long reservationId) {
        return changeScore(userId, reservationId, ChangeType.TIMEOUT, TIMEOUT_SCORE, "超时未核销扣分");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deductScoreForCancel(Long userId, Long reservationId) {
        return changeScore(userId, reservationId, ChangeType.CANCEL, CANCEL_SCORE, "临时取消订单扣分");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deductScoreForComplaint(Long userId, Long reservationId, String remark) {
        return changeScore(userId, reservationId, ChangeType.COMPLAINT, COMPLAINT_SCORE, "被投诉扣分: " + remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int adjustScore(Long userId, int changeValue, String remark) {
        return changeScore(userId, null, ChangeType.SYSTEM_ADJUST, changeValue, "系统调整: " + remark);
    }

    /**
     * 通用分数变动方法
     */
    private int changeScore(Long userId, Long reservationId, int changeType, int changeValue, String remark) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        int beforeScore = user.getCreditScore() != null ? user.getCreditScore() : DEFAULT_SCORE;
        int afterScore = beforeScore + changeValue;

        // 限制分数范围
        afterScore = Math.max(MIN_SCORE, Math.min(MAX_SCORE, afterScore));

        // 更新用户信用分
        user.setCreditScore(afterScore);
        userMapper.updateById(user);

        // 记录信用变动
        CreditRecord record = new CreditRecord();
        record.setUserId(userId);
        record.setReservationId(reservationId);
        record.setChangeType(changeType);
        record.setChangeValue(changeValue);
        record.setBeforeScore(beforeScore);
        record.setAfterScore(afterScore);
        record.setRemark(remark);
        creditRecordMapper.insert(record);

        log.info("信用分变动: userId={}, changeType={}, changeValue={}, before={}, after={}",
                userId, changeType, changeValue, beforeScore, afterScore);

        return afterScore;
    }

    @Override
    public int getCurrentScore(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getCreditScore() != null ? user.getCreditScore() : DEFAULT_SCORE;
    }

    @Override
    public Page<CreditRecord> getRecords(Long userId, Page<CreditRecord> page) {
        LambdaQueryWrapper<CreditRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CreditRecord::getUserId, userId)
                .orderByDesc(CreditRecord::getCreatedAt);
        return creditRecordMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean checkCredit(Long userId, int requiredScore) {
        return getCurrentScore(userId) >= requiredScore;
    }
}
