package com.graduation.platform.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.platform.common.BusinessException;
import com.graduation.platform.common.ResultCode;
import com.graduation.platform.mapper.CreditRecordMapper;
import com.graduation.platform.mapper.UserMapper;
import com.graduation.platform.mapper.WalletMapper;
import com.graduation.platform.model.entity.CreditRecord;
import com.graduation.platform.model.entity.User;
import com.graduation.platform.model.entity.Wallet;
import com.graduation.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final WalletMapper walletMapper;
    private final CreditRecordMapper creditRecordMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${wechat.appid:}")
    private String appid;

    @Value("${wechat.secret:}")
    private String secret;

    @Value("${parking.rules.min-credit-score:60}")
    private Integer minCreditScore;

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    @Transactional
    public User wxLogin(String code) {
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WX_LOGIN_URL, appid, secret, code);

        String response = HttpUtil.get(url);
        JSONObject jsonObject = JSON.parseObject(response);

        if (jsonObject.containsKey("errcode") && jsonObject.getIntValue("errcode") != 0) {
            log.error("WeChat login failed: {}", response);
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String openid = jsonObject.getString("openid");
        User user = getByOpenid(openid);

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname("User" + openid.substring(Math.max(openid.length() - 6, 0)));
            user.setStatus(1);
            user.setCreditScore(100);
            user.setIsOwner(0);
            user.setIsVerified(0);
            user.setDeleted(0);
            userMapper.insert(user);

            createDefaultWallet(user.getId());
            log.info("New user registered via WeChat: userId={}, openid={}", user.getId(), openid);
        }

        return user;
    }

    @Override
    @Transactional
    public User register(String username, String password, String phone, String nickname) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }
        if (password.length() < 6) {
            throw new BusinessException(400, "密码长度不能少于6位");
        }

        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, username).eq(User::getDeleted, 0);
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        if (StringUtils.hasText(phone)) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, phone).eq(User::getDeleted, 0);
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException(400, "手机号已被注册");
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(StringUtils.hasText(phone) ? phone : null);
        user.setNickname(StringUtils.hasText(nickname) ? nickname : username);
        user.setStatus(1);
        user.setCreditScore(100);
        user.setIsOwner(0);
        user.setIsVerified(0);
        user.setDeleted(0);
        userMapper.insert(user);

        createDefaultWallet(user.getId());
        log.info("New user registered: userId={}, username={}", user.getId(), username);
        return user;
    }

    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username).eq(User::getDeleted, 0);
        User user = userMapper.selectOne(wrapper);

        if (user == null || !StringUtils.hasText(user.getPassword()) || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        return user;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        return user;
    }

    @Override
    public User getByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid).eq(User::getDeleted, 0);
        return userMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        existUser.setNickname(user.getNickname());
        existUser.setAvatar(user.getAvatar());
        existUser.setPhone(user.getPhone());
        return userMapper.updateById(existUser) > 0;
    }

    @Override
    @Transactional
    public boolean verifyRealName(Long userId, String realName, String idCard) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        user.setRealName(realName);
        user.setIdCard(idCard);
        user.setIsVerified(1);
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean updateCreditScore(Long userId, Integer delta, String reason) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        int oldScore = user.getCreditScore();
        int newScore = Math.max(0, Math.min(100, oldScore + delta));

        user.setCreditScore(newScore);
        userMapper.updateById(user);

        CreditRecord record = new CreditRecord();
        record.setUserId(userId);
        record.setChangeType(5);
        record.setChangeValue(delta);
        record.setBeforeScore(oldScore);
        record.setAfterScore(newScore);
        record.setRemark(reason);
        creditRecordMapper.insert(record);

        log.info("Credit score updated: userId={}, {} -> {}, reason={}", userId, oldScore, newScore, reason);
        return true;
    }

    @Override
    public boolean checkCreditScore(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return user.getCreditScore() >= minCreditScore;
    }

    private void createDefaultWallet(Long userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setFrozenAmount(BigDecimal.ZERO);
        wallet.setTotalIncome(BigDecimal.ZERO);
        wallet.setTotalWithdraw(BigDecimal.ZERO);
        walletMapper.insert(wallet);
    }
}
