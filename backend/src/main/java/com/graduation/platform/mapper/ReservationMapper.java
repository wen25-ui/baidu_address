package com.graduation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.platform.model.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约订单Mapper接口
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
}
