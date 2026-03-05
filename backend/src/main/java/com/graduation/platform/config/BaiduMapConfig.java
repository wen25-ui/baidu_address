package com.graduation.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baidu.mapapi.SDKInitializer;

@Configuration
public class BaiduMapConfig {

    @Bean
    public void initializeBaiduMap() {
        // Initialize Baidu Map SDK
        SDKInitializer.initialize();
    }
}