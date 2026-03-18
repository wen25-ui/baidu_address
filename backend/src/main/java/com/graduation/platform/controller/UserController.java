package com.graduation.platform.controller;

import com.graduation.platform.common.Result;
import com.graduation.platform.model.entity.User;
import com.graduation.platform.service.UserService;
import com.graduation.platform.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * User controller for mini program.
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * WeChat mini-program login.
     */
    @PostMapping("/wx-login")
    public Result<Map<String, Object>> wxLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        if (code == null || code.isEmpty()) {
            return Result.error(400, "Code is required");
        }

        User user = userService.wxLogin(code);
        String token = jwtUtils.generateToken(user.getId(), "user");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    /**
     * Username/password register.
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request.username(), request.password(), request.phone(), request.nickname());
        String token = jwtUtils.generateToken(user.getId(), "user");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    /**
     * Username/password login.
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.username(), request.password());
        String token = jwtUtils.generateToken(user.getId(), "user");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    /**
     * Get current user profile.
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestAttribute("userId") Long userId) {
        User user = userService.getById(userId);
        return Result.success(user);
    }

    /**
     * Update current user profile.
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(
            @RequestAttribute("userId") Long userId,
            @RequestBody User user) {
        user.setId(userId);
        userService.updateUser(user);
        return Result.success(null);
    }

    /**
     * Real-name verification.
     */
    @PostMapping("/verify")
    public Result<Void> verifyRealName(
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, String> params) {
        String realName = params.get("realName");
        String idCard = params.get("idCard");

        if (realName == null || idCard == null) {
            return Result.error(400, "Real name and ID card are required");
        }

        userService.verifyRealName(userId, realName, idCard);
        return Result.success(null);
    }

    /**
     * Credit score check.
     */
    @GetMapping("/credit-check")
    public Result<Map<String, Object>> checkCredit(@RequestAttribute("userId") Long userId) {
        User user = userService.getById(userId);
        boolean canReserve = userService.checkCreditScore(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("creditScore", user.getCreditScore());
        data.put("canReserve", canReserve);

        return Result.success(data);
    }

    public record LoginRequest(String username, String password) {}
    public record RegisterRequest(String username, String password, String phone, String nickname) {}
}
