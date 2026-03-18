package com.graduation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.platform.model.entity.ParkingSpaceRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车位可用时段规则Mapper接口
 */
@Mapper
public interface ParkingSpaceRuleMapper extends BaseMapper<ParkingSpaceRule> {
}
