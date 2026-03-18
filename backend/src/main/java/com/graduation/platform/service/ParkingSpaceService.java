package com.graduation.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.model.entity.ParkingSpace;

import java.math.BigDecimal;
import java.util.List;

/**
 * 车位服务接口
 */
public interface ParkingSpaceService {

    /**
     * 发布车位
     * @param parkingSpace 车位信息
     * @return 车位ID
     */
    Long publish(ParkingSpace parkingSpace);

    /**
     * 根据ID获取车位详情
     * @param id 车位ID
     * @return 车位信息
     */
    ParkingSpace getById(Long id);

    /**
     * 更新车位信息
     * @param parkingSpace 车位信息
     * @return 是否成功
     */
    boolean update(ParkingSpace parkingSpace);

    /**
     * 删除车位（软删除）
     * @param id 车位ID
     * @param ownerId 车位主人ID（验证权限）
     * @return 是否成功
     */
    boolean delete(Long id, Long ownerId);

    /**
     * 上架车位
     * @param id 车位ID
     * @param ownerId 车位主人ID
     * @return 是否成功
     */
    boolean online(Long id, Long ownerId);

    /**
     * 下架车位
     * @param id 车位ID
     * @param ownerId 车位主人ID
     * @return 是否成功
     */
    boolean offline(Long id, Long ownerId);

    /**
     * 根据地理位置搜索附近车位
     * @param latitude 纬度
     * @param longitude 经度
     * @param radiusKm 搜索半径（公里）
     * @param page 分页参数
     * @return 车位列表
     */
    Page<ParkingSpace> searchNearby(BigDecimal latitude, BigDecimal longitude, 
                                     Double radiusKm, Page<ParkingSpace> page);

    /**
     * 获取车位主人的所有车位
     * @param ownerId 车位主人ID
     * @param page 分页参数
     * @return 车位列表
     */
    Page<ParkingSpace> getByOwnerId(Long ownerId, Page<ParkingSpace> page);

    /**
     * 关键词搜索车位（地址、小区名称）
     * @param keyword 关键词
     * @param page 分页参数
     * @return 车位列表
     */
    Page<ParkingSpace> searchByKeyword(String keyword, Page<ParkingSpace> page);

    /**
     * 获取车位统计信息
     * @param ownerId 车位主人ID
     * @return 统计数据
     */
    ParkingSpaceStats getStats(Long ownerId);

    /**
     * 车位统计信息
     */
    record ParkingSpaceStats(
        int totalCount,
        int onlineCount,
        int offlineCount,
        BigDecimal totalIncome
    ) {}
}
