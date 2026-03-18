package com.graduation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.platform.model.entity.Wallet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户钱包Mapper接口
 */
@Mapper
public interface WalletMapper extends BaseMapper<Wallet> {
}
