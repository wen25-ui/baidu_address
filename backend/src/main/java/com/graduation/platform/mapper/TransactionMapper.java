package com.graduation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.platform.model.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易流水Mapper接口
 */
@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
}
