package com.graduation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.platform.model.entity.ParkingSpace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车位Mapper接口
 */
@Mapper
public interface ParkingSpaceMapper extends BaseMapper<ParkingSpace> {
}
