package com.graduation.platform.service;

import com.graduation.platform.model.entity.User;

/**
 * User service.
 */
public interface UserService {

    /**
     * WeChat mini-program login.
     */
    User wxLogin(String code);

    /**
     * Register with username/password.
     */
    User register(String username, String password, String phone, String nickname);

    /**
     * Username/password login.
     */
    User login(String username, String password);

    /**
     * Get user by id.
     */
    User getById(Long id);

    /**
     * Get user by openid.
     */
    User getByOpenid(String openid);

    /**
     * Update user profile.
     */
    boolean updateUser(User user);

    /**
     * Verify real name.
     */
    boolean verifyRealName(Long userId, String realName, String idCard);

    /**
     * Update user credit score.
     */
    boolean updateCreditScore(Long userId, Integer delta, String reason);

    /**
     * Check whether user has enough credit score.
     */
    boolean checkCreditScore(Long userId);
}
