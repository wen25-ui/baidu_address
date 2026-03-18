package com.graduation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.platform.model.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏Mapper接口
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
}
