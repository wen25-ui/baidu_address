package com.graduation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.platform.common.BusinessException;
import com.graduation.platform.common.ResultCode;
import com.graduation.platform.mapper.ParkingSpaceMapper;
import com.graduation.platform.mapper.ReservationMapper;
import com.graduation.platform.model.entity.ParkingSpace;
import com.graduation.platform.service.ParkingSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private final ParkingSpaceMapper parkingSpaceMapper;
    private final ReservationMapper reservationMapper;

    // 车位状态常量
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_ONLINE = 1;
    private static final int STATUS_OFFLINE = 2;

    @Override
    @Transactional
    public Long publish(ParkingSpace parkingSpace) {
        parkingSpace.setStatus(STATUS_PENDING);
        parkingSpace.setDeleted(0);
        parkingSpaceMapper.insert(parkingSpace);
        log.info("Parking space published: id={}, ownerId={}", parkingSpace.getId(), parkingSpace.getOwnerId());
        return parkingSpace.getId();
    }

    @Override
    public ParkingSpace getById(Long id) {
        ParkingSpace space = parkingSpaceMapper.selectById(id);
        if (space == null || space.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return space;
    }

    @Override
    @Transactional
    public boolean update(ParkingSpace parkingSpace) {
        ParkingSpace existing = getById(parkingSpace.getId());
        existing.setTitle(parkingSpace.getTitle());
        existing.setDescription(parkingSpace.getDescription());
        existing.setCommunityName(parkingSpace.getCommunityName());
        existing.setAddress(parkingSpace.getAddress());
        existing.setLongitude(parkingSpace.getLongitude());
        existing.setLatitude(parkingSpace.getLatitude());
        existing.setSpaceNumber(parkingSpace.getSpaceNumber());
        existing.setPricePerHour(parkingSpace.getPricePerHour());
        existing.setImages(parkingSpace.getImages());
        return parkingSpaceMapper.updateById(existing) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id, Long ownerId) {
        ParkingSpace space = getById(id);
        if (!space.getOwnerId().equals(ownerId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        space.setDeleted(1);
        return parkingSpaceMapper.updateById(space) > 0;
    }

    @Override
    @Transactional
    public boolean online(Long id, Long ownerId) {
        ParkingSpace space = getById(id);
        if (!space.getOwnerId().equals(ownerId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (space.getStatus() == STATUS_PENDING) {
            throw new BusinessException(ResultCode.SPACE_AUDIT_PENDING);
        }
        space.setStatus(STATUS_ONLINE);
        return parkingSpaceMapper.updateById(space) > 0;
    }

    @Override
    @Transactional
    public boolean offline(Long id, Long ownerId) {
        ParkingSpace space = getById(id);
        if (!space.getOwnerId().equals(ownerId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        space.setStatus(STATUS_OFFLINE);
        return parkingSpaceMapper.updateById(space) > 0;
    }

    @Override
    public Page<ParkingSpace> searchNearby(BigDecimal latitude, BigDecimal longitude,
                                           Double radiusKm, Page<ParkingSpace> page) {
        // Use Haversine formula to calculate distance
        // Earth radius in km
        double earthRadius = 6371.0;
        double latDelta = radiusKm / earthRadius * (180 / Math.PI);
        double lngDelta = radiusKm / (earthRadius * Math.cos(Math.toRadians(latitude.doubleValue()))) * (180 / Math.PI);

        BigDecimal minLat = latitude.subtract(BigDecimal.valueOf(latDelta));
        BigDecimal maxLat = latitude.add(BigDecimal.valueOf(latDelta));
        BigDecimal minLng = longitude.subtract(BigDecimal.valueOf(lngDelta));
        BigDecimal maxLng = longitude.add(BigDecimal.valueOf(lngDelta));

        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingSpace::getStatus, STATUS_ONLINE)
               .eq(ParkingSpace::getDeleted, 0)
               .between(ParkingSpace::getLatitude, minLat, maxLat)
               .between(ParkingSpace::getLongitude, minLng, maxLng)
               .orderByAsc(ParkingSpace::getPricePerHour);

        return parkingSpaceMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<ParkingSpace> getByOwnerId(Long ownerId, Page<ParkingSpace> page) {
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingSpace::getOwnerId, ownerId)
               .eq(ParkingSpace::getDeleted, 0)
               .orderByDesc(ParkingSpace::getCreatedAt);
        return parkingSpaceMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<ParkingSpace> searchByKeyword(String keyword, Page<ParkingSpace> page) {
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingSpace::getStatus, STATUS_ONLINE)
               .eq(ParkingSpace::getDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ParkingSpace::getCommunityName, keyword)
                             .or()
                             .like(ParkingSpace::getAddress, keyword)
                             .or()
                             .like(ParkingSpace::getTitle, keyword));
        }
        
        wrapper.orderByDesc(ParkingSpace::getCreatedAt);
        return parkingSpaceMapper.selectPage(page, wrapper);
    }

    @Override
    public ParkingSpaceStats getStats(Long ownerId) {
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingSpace::getOwnerId, ownerId)
               .eq(ParkingSpace::getDeleted, 0);
        
        Long total = parkingSpaceMapper.selectCount(wrapper);
        
        wrapper.eq(ParkingSpace::getStatus, STATUS_ONLINE);
        Long online = parkingSpaceMapper.selectCount(wrapper);
        
        // TODO: Calculate total income from reservations
        BigDecimal totalIncome = BigDecimal.ZERO;
        
        return new ParkingSpaceStats(
            total.intValue(),
            online.intValue(),
            total.intValue() - online.intValue(),
            totalIncome
        );
    }
}