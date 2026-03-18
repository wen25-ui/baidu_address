package com.graduation.platform.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.platform.mapper.ParkingSpaceMapper;
import com.graduation.platform.mapper.UserMapper;
import com.graduation.platform.mapper.WalletMapper;
import com.graduation.platform.model.entity.ParkingSpace;
import com.graduation.platform.model.entity.User;
import com.graduation.platform.model.entity.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seed Chengdu parking demo data for local testing.
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class ChengduTestDataSeeder implements ApplicationRunner {

    private final UserMapper userMapper;
    private final WalletMapper walletMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        List<OwnerSeed> owners = List.of(
                new OwnerSeed("cd_owner_1", "成都车位主1", "13800000001"),
                new OwnerSeed("cd_owner_2", "成都车位主2", "13800000002")
        );

        Long owner1Id = ensureOwner(owners.get(0));
        Long owner2Id = ensureOwner(owners.get(1));

        List<ParkingSeed> parkingSeeds = List.of(
                new ParkingSeed(owner1Id, "成都IFS地下停车位A1", "锦江区IFS", "锦江区红星路三段1号", "104.0828600", "30.6586100", "A1", "15.00"),
                new ParkingSeed(owner1Id, "春熙路停车位B2", "春熙路商圈", "锦江区总府路31号", "104.0785700", "30.6571700", "B2", "12.00"),
                new ParkingSeed(owner1Id, "太古里停车位C8", "太古里", "锦江区中纱帽街8号", "104.0821200", "30.6547600", "C8", "14.00"),
                new ParkingSeed(owner2Id, "天府广场停车位D3", "天府广场", "青羊区人民东路6号", "104.0665400", "30.6570400", "D3", "10.00"),
                new ParkingSeed(owner2Id, "成都东站停车位E5", "成都东站", "成华区邛崃山路333号", "104.1489300", "30.6295400", "E5", "8.00"),
                new ParkingSeed(owner2Id, "金融城停车位F6", "高新区金融城", "高新区天府大道北段966号", "104.0622100", "30.5717400", "F6", "11.00")
        );

        parkingSeeds.forEach(this::ensureParkingSpace);
    }

    private Long ensureOwner(OwnerSeed seed) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, seed.username).eq(User::getDeleted, 0);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            user = new User();
            user.setUsername(seed.username);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setPhone(seed.phone);
            user.setNickname(seed.nickname);
            user.setStatus(1);
            user.setCreditScore(100);
            user.setIsOwner(1);
            user.setIsVerified(1);
            user.setDeleted(0);
            userMapper.insert(user);
            log.info("Seeded test owner: username={}, userId={}", seed.username, user.getId());
        } else {
            user.setIsOwner(1);
            userMapper.updateById(user);
        }

        ensureWallet(user.getId());
        return user.getId();
    }

    private void ensureWallet(Long userId) {
        LambdaQueryWrapper<Wallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = walletMapper.selectOne(wrapper);

        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozenAmount(BigDecimal.ZERO);
            wallet.setTotalIncome(BigDecimal.ZERO);
            wallet.setTotalWithdraw(BigDecimal.ZERO);
            walletMapper.insert(wallet);
        }
    }

    private void ensureParkingSpace(ParkingSeed seed) {
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingSpace::getOwnerId, seed.ownerId)
                .eq(ParkingSpace::getTitle, seed.title)
                .eq(ParkingSpace::getDeleted, 0);

        if (parkingSpaceMapper.selectCount(wrapper) > 0) {
            return;
        }

        ParkingSpace space = new ParkingSpace();
        space.setOwnerId(seed.ownerId);
        space.setTitle(seed.title);
        space.setDescription("成都测试车位，用于地图搜索与导航联调");
        space.setCommunityName(seed.communityName);
        space.setAddress(seed.address);
        space.setLongitude(new BigDecimal(seed.longitude));
        space.setLatitude(new BigDecimal(seed.latitude));
        space.setSpaceNumber(seed.spaceNumber);
        space.setPricePerHour(new BigDecimal(seed.pricePerHour));
        space.setImages("[\"/static/images/default-parking.png\"]");
        space.setStatus(1);
        space.setDeleted(0);

        parkingSpaceMapper.insert(space);
        log.info("Seeded test parking space: {}", seed.title);
    }

    private record OwnerSeed(String username, String nickname, String phone) {}

    private record ParkingSeed(
            Long ownerId,
            String title,
            String communityName,
            String address,
            String longitude,
            String latitude,
            String spaceNumber,
            String pricePerHour
    ) {}
}
