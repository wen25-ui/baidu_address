package com.graduation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.platform.model.entity.CreditRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 信用分变动记录Mapper接口
 */
@Mapper
public interface CreditRecordMapper extends BaseMapper<CreditRecord> {
}
