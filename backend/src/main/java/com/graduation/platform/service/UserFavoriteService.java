package com.graduation.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.model.entity.ParkingSpace;

/**
 * 用户收藏服务接口
 */
public interface UserFavoriteService {

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param spaceId 车位ID
     * @return 收藏ID
     */
    Long add(Long userId, Long spaceId);

    /**
     * 取消收藏
     * @param userId 用户ID
     * @param spaceId 车位ID
     * @return 是否成功
     */
    boolean remove(Long userId, Long spaceId);

    /**
     * 检查是否已收藏
     * @param userId 用户ID
     * @param spaceId 车位ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long spaceId);

    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @param page 分页参数
     * @return 收藏的车位列表
     */
    Page<ParkingSpace> getFavorites(Long userId, Page<ParkingSpace> page);

    /**
     * 获取用户收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    int getFavoriteCount(Long userId);

    /**
     * 获取车位被收藏次数
     * @param spaceId 车位ID
     * @return 被收藏次数
     */
    int getSpaceFavoriteCount(Long spaceId);
}
