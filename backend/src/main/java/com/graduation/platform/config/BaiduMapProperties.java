package com.graduation.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 百度地图配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "baidu.map")
public class BaiduMapProperties {
    
    /**
     * 微信小程序AK
     */
    private String wxAk;
    
    /**
     * 浏览器端AK
     */
    private String browserAk;
    
    /**
     * API地址
     */
    private String apiUrl;
}
