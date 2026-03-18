package com.graduation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.platform.common.BusinessException;
import com.graduation.platform.common.ResultCode;
import com.graduation.platform.mapper.ParkingSpaceMapper;
import com.graduation.platform.mapper.ParkingSpaceRuleMapper;
import com.graduation.platform.model.entity.ParkingSpace;
import com.graduation.platform.model.entity.ParkingSpaceRule;
import com.graduation.platform.service.ParkingSpaceRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingSpaceRuleServiceImpl implements ParkingSpaceRuleService {

    private final ParkingSpaceRuleMapper ruleMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;

    @Override
    @Transactional
    public Long addRule(ParkingSpaceRule rule) {
        rule.setDeleted(0);
        ruleMapper.insert(rule);
        log.info("Rule added: id={}, spaceId={}", rule.getId(), rule.getSpaceId());
        return rule.getId();
    }

    @Override
    @Transactional
    public int batchAddRules(List<ParkingSpaceRule> rules) {
        int count = 0;
        for (ParkingSpaceRule rule : rules) {
            rule.setDeleted(0);
            ruleMapper.insert(rule);
            count++;
        }
        return count;
    }

    @Override
    public List<ParkingSpaceRule> getByParkingSpaceId(Long parkingSpaceId) {
        LambdaQueryWrapper<ParkingSpaceRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingSpaceRule::getSpaceId, parkingSpaceId)
               .eq(ParkingSpaceRule::getDeleted, 0)
               .orderByAsc(ParkingSpaceRule::getStartTime);
        return ruleMapper.selectList(wrapper);
    }

    @Override
    public List<ParkingSpaceRule> getAvailableRules(Long parkingSpaceId, LocalDate date) {
        List<ParkingSpaceRule> allRules = getByParkingSpaceId(parkingSpaceId);
        int dayOfWeek = date.getDayOfWeek().getValue();
        
        return allRules.stream()
            .filter(rule -> rule.getIsAvailable() == 1)
            .filter(rule -> {
                if (rule.getRuleType() == 1) {
                    // Weekly repeat rule
                    String[] days = rule.getDayOfWeek().split(",");
                    for (String day : days) {
                        if (Integer.parseInt(day.trim()) == dayOfWeek) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    // Specific date rule
                    return date.equals(rule.getSpecificDate());
                }
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateRule(ParkingSpaceRule rule) {
        ParkingSpaceRule existing = ruleMapper.selectById(rule.getId());
        if (existing == null || existing.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return ruleMapper.updateById(rule) > 0;
    }

    @Override
    @Transactional
    public boolean deleteRule(Long ruleId, Long ownerId) {
        ParkingSpaceRule rule = ruleMapper.selectById(ruleId);
        if (rule == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        ParkingSpace space = parkingSpaceMapper.selectById(rule.getSpaceId());
        if (space == null || !space.getOwnerId().equals(ownerId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        
        rule.setDeleted(1);
        return ruleMapper.updateById(rule) > 0;
    }

    @Override
    @Transactional
    public int deleteByParkingSpaceId(Long parkingSpaceId) {
        LambdaQueryWrapper<ParkingSpaceRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingSpaceRule::getSpaceId, parkingSpaceId);
        
        ParkingSpaceRule update = new ParkingSpaceRule();
        update.setDeleted(1);
        return ruleMapper.update(update, wrapper);
    }

    @Override
    public boolean checkAvailable(Long parkingSpaceId, LocalDate date, int startHour, int endHour) {
        List<ParkingSpaceRule> rules = getAvailableRules(parkingSpaceId, date);
        
        for (ParkingSpaceRule rule : rules) {
            int ruleStart = rule.getStartTime().getHour();
            int ruleEnd = rule.getEndTime().getHour();
            
            // Check if requested time is within rule time
            if (startHour >= ruleStart && endHour <= ruleEnd) {
                return true;
            }
        }
        return false;
    }
}