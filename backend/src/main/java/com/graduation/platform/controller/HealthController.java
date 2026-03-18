package com.graduation.platform.controller;

import com.graduation.platform.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查接口
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("service", "parking-share-platform");
        data.put("version", "1.0.0");
        return Result.success(data);
    }

    @GetMapping("/api/public/health")
    public Result<Map<String, Object>> publicHealth() {
        return health();
    }
}
