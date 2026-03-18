package com.graduation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.platform.model.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员Mapper接口
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
