package com.graduation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.BusinessException;
import com.graduation.platform.mapper.ParkingSpaceMapper;
import com.graduation.platform.mapper.UserFavoriteMapper;
import com.graduation.platform.model.entity.ParkingSpace;
import com.graduation.platform.model.entity.UserFavorite;
import com.graduation.platform.service.UserFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(Long userId, Long spaceId) {
        // 检查车位是否存在
        ParkingSpace space = parkingSpaceMapper.selectById(spaceId);
        if (space == null) {
            throw new BusinessException("车位不存在");
        }

        // 检查是否已收藏
        if (isFavorite(userId, spaceId)) {
            throw new BusinessException("已经收藏过了");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setSpaceId(spaceId);
        userFavoriteMapper.insert(favorite);

        log.info("添加收藏: userId={}, spaceId={}", userId, spaceId);
        return favorite.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long userId, Long spaceId) {
        int deleted = userFavoriteMapper.delete(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getSpaceId, spaceId)
        );
        
        if (deleted > 0) {
            log.info("取消收藏: userId={}, spaceId={}", userId, spaceId);
        }
        return deleted > 0;
    }

    @Override
    public boolean isFavorite(Long userId, Long spaceId) {
        return userFavoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getSpaceId, spaceId)
        ) > 0;
    }

    @Override
    public Page<ParkingSpace> getFavorites(Long userId, Page<ParkingSpace> page) {
        // 获取用户收藏的车位ID列表
        List<UserFavorite> favorites = userFavoriteMapper.selectList(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .orderByDesc(UserFavorite::getCreatedAt)
        );

        if (favorites.isEmpty()) {
            return page;
        }

        List<Long> spaceIds = favorites.stream()
                .map(UserFavorite::getSpaceId)
                .toList();

        // 查询车位信息
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ParkingSpace::getId, spaceIds);
        
        return parkingSpaceMapper.selectPage(page, wrapper);
    }

    @Override
    public int getFavoriteCount(Long userId) {
        return userFavoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
        ).intValue();
    }

    @Override
    public int getSpaceFavoriteCount(Long spaceId) {
        return userFavoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getSpaceId, spaceId)
        ).intValue();
    }
}
